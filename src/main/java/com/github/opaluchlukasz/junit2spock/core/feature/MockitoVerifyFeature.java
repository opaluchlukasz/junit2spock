package com.github.opaluchlukasz.junit2spock.core.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.TIMES;

public class MockitoVerifyFeature extends Feature<MethodInvocation> {

    private static final Logger LOG = LoggerFactory.getLogger(MockitoVerifyFeature.class);

    public static final String VERIFY = "verify";

    private final ASTNodeFactory astNodeFactory;

    MockitoVerifyFeature(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
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
        return astNodeFactory.infixExpression(TIMES,
                numberOfInvocation(arguments.size() == 2 ? Optional.of(arguments.get(1)) : empty()),
                astNodeFactory.methodInvocation(parentMethodInvocation.getName().getFullyQualifiedName(),
                        (List<ASTNode>) parentMethodInvocation.arguments().stream()
                                .map(astNodeFactory::clone).collect(toList()),
                        (Expression) astNodeFactory.clone(arguments.get(0))));
    }

    private Expression numberOfInvocation(Optional<Object> arg) {
        return arg
                .filter(astNode -> astNode instanceof MethodInvocation)
                .map(astNode -> (MethodInvocation) astNode)
                .flatMap(this::numberOfInvocation)
                .orElse(astNodeFactory.simpleName("_"));
    }

    private Optional<Expression> numberOfInvocation(MethodInvocation methodInvocation) {
        if (methodInvocation.arguments().isEmpty()) {
            if (methodInvocation.getName().getFullyQualifiedName().equals("never")) {
                return Optional.of(astNodeFactory.numberLiteral("0"));
            }
        }
        if (methodInvocation.arguments().size() == 1) {
            if (methodInvocation.getName().getFullyQualifiedName().equals("times")) {
                return Optional.of((Expression) astNodeFactory.clone(methodInvocation.arguments().get(0)));
            }
        }
        LOG.warn(format("Unsupported VerificationMode: %s", methodInvocation.getName().getFullyQualifiedName()));
        return empty();
    }
}
