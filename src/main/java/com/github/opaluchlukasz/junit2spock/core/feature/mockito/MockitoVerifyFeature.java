package com.github.opaluchlukasz.junit2spock.core.feature.mockito;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.feature.Feature;
import com.google.common.collect.ImmutableMap;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.node.CustomInfixOperator.CAST;
import static com.github.opaluchlukasz.junit2spock.core.node.CustomInfixOperator.RANGE;
import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.TIMES;

public class MockitoVerifyFeature extends Feature<MethodInvocation> {

    private static final Logger LOG = LoggerFactory.getLogger(MockitoVerifyFeature.class);

    public static final String VERIFY = "verify";
    private static final Map<String, String> MATCHER_TYPE_OVERRIDE = ImmutableMap.of("Char", "Character", "Int", "Integer");

    private final ASTNodeFactory nodeFactory;

    public MockitoVerifyFeature(ASTNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    @Override
    public Optional<MethodInvocation> applicable(Object astNode) {
        return Optional.of(astNode)
                .filter(statement -> statement instanceof ExpressionStatement)
                .map(statement -> ((ExpressionStatement) statement).getExpression())
                .filter(expression -> expression instanceof MethodInvocation)
                .map(expression -> ((MethodInvocation) expression).getExpression())
                .flatMap(expression -> methodInvocation(expression, VERIFY))
                .filter(invocation -> invocation.arguments().size() == 1 || invocation.arguments().size() == 2);
    }

    @Override
    public InfixExpression apply(Object object, MethodInvocation verifyMethodInvocation) {
        List arguments = verifyMethodInvocation.arguments();
        MethodInvocation parentMethodInvocation = (MethodInvocation) verifyMethodInvocation.getParent();
        return nodeFactory.infixExpression(TIMES,
                cardinality(arguments.size() == 2 ? Optional.of(arguments.get(1)) : empty()),
                nodeFactory.methodInvocation(parentMethodInvocation.getName().getFullyQualifiedName(),
                        (List<ASTNode>) parentMethodInvocation.arguments().stream()
                                .map(this::applyMatchers).collect(toList()),
                        nodeFactory.clone((Expression) arguments.get(0))));
    }

    private Object applyMatchers(Object argument) {
        return methodInvocation(argument).map(methodInvocation -> {
            switch (methodInvocation.getName().getIdentifier()) {
                case "anyObject":
                    return wildcard();
                case "any":
                    return anyMatcher(methodInvocation);
                case "anyBoolean":
                case "anyByte":
                case "anyChar":
                case "anyInt":
                case "anyLong":
                case "anyFloat":
                case "anyDouble":
                case "anyShort":
                case "anyString":
                case "anyList":
                case "anySet":
                case "anyMap":
                case "anyIterable":
                case "anyCollection":
                    return classMatcher(getClass(methodInvocation));
                case "isA":
                    return anyMatcher(methodInvocation);
                default:
                    return nodeFactory.clone((ASTNode) argument);
            }
        }).orElseGet(() -> nodeFactory.clone((ASTNode) argument));
    }

    private Object anyMatcher(MethodInvocation methodInvocation) {
        if (methodInvocation.arguments().size() == 1 && methodInvocation.arguments().get(0) instanceof TypeLiteral) {
            return classMatcher(((TypeLiteral) methodInvocation.arguments().get(0)).getType().toString());
        } else {
            return wildcard();
        }
    }

    private String getClass(MethodInvocation methodInv) {
        String type = methodInv.getName().getIdentifier().replaceFirst("any", "");
        return MATCHER_TYPE_OVERRIDE.getOrDefault(type, type);
    }

    private Object classMatcher(String type) {
        TypeLiteral classLiteral = nodeFactory.typeLiteral(nodeFactory.simpleType(nodeFactory.simpleName(type)));
        return nodeFactory.infixExpression(CAST, wildcard(), classLiteral);
    }

    private SimpleName wildcard() {
        return nodeFactory.simpleName("_");
    }

    private Expression cardinality(Optional<Object> arg) {
        return arg
                .filter(astNode -> astNode instanceof MethodInvocation)
                .map(astNode -> (MethodInvocation) astNode)
                .flatMap(this::cardinality)
                .orElse(nodeFactory.numberLiteral("1"));
    }

    private Optional<Expression> cardinality(MethodInvocation methodInvocation) {
        if (methodInvocation.arguments().isEmpty()) {
            if (methodInvocation.getName().getFullyQualifiedName().equals("never")) {
                return Optional.of(nodeFactory.numberLiteral("0"));
            }
            if (methodInvocation.getName().getFullyQualifiedName().equals("atLeastOnce")) {
                return Optional.of(nodeFactory.parenthesizedExpression(nodeFactory
                        .infixExpression(
                                RANGE,
                                nodeFactory.numberLiteral("1"),
                                wildcard())));
            }
        }
        if (methodInvocation.arguments().size() == 1) {
            if (methodInvocation.getName().getFullyQualifiedName().equals("times")) {
                return Optional.of(nodeFactory.clone((Expression) methodInvocation.arguments().get(0)));
            }
            if (methodInvocation.getName().getFullyQualifiedName().equals("atLeast")) {
                return Optional.of(nodeFactory.parenthesizedExpression(nodeFactory
                        .infixExpression(
                                RANGE,
                                nodeFactory.clone((Expression) methodInvocation.arguments().get(0)),
                                wildcard())));
            }
            if (methodInvocation.getName().getFullyQualifiedName().equals("atMost")) {
                return Optional.of(nodeFactory.parenthesizedExpression(nodeFactory
                        .infixExpression(
                                RANGE,
                                wildcard(),
                                nodeFactory.clone((Expression) methodInvocation.arguments().get(0)))));
            }
        }
        LOG.warn(format("Unsupported VerificationMode: %s", methodInvocation.getName().getFullyQualifiedName()));
        return empty();
    }
}
