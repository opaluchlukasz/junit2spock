package com.github.opaluchlukasz.junit2spock.core.feature.mockito;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.feature.Feature;
import com.github.opaluchlukasz.junit2spock.core.node.GroovyClosureBuilder;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;
import static java.util.stream.Collectors.toList;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.RIGHT_SHIFT_SIGNED;

public class MockitoThrowFeature extends Feature<MethodInvocation> {

    private final ASTNodeFactory nodeFactory;
    private final MatcherHandler matcherHandler;
    private final GroovyClosureBuilder groovyClosureBuilder;
    private final String when;
    private final String thenThrow;

    MockitoThrowFeature(ASTNodeFactory nodeFactory, MatcherHandler matcherHandler,
                        GroovyClosureBuilder groovyClosureBuilder, String when, String thenThrow) {
        this.nodeFactory = nodeFactory;
        this.matcherHandler = matcherHandler;
        this.groovyClosureBuilder = groovyClosureBuilder;
        this.when = when;
        this.thenThrow = thenThrow;
    }

    @Override
    public Optional<MethodInvocation> applicable(Object astNode) {
        return methodInvocation(astNode, thenThrow)
                .flatMap(thenReturnInvocation -> methodInvocation(thenReturnInvocation.getExpression(), when))
                .filter(whenInvocation -> whenInvocation.arguments().size() == 1);
    }

    @Override
    public Expression apply(Object object, MethodInvocation whenMethodInvocation) {
        MethodInvocation methodInvocation = methodInvocation(object, thenThrow).get();
        List arguments = methodInvocation.arguments();
        if (arguments.size() == 1) {
            MethodInvocation mockedMethodInvocation = (MethodInvocation) whenMethodInvocation.arguments().get(0);
            Expression toBeThrown = argumentAsExpression(arguments.get(0));
            Expression throwingClosure = groovyClosureBuilder.aClosure()
                    .withBodyStatement(nodeFactory.throwStatement(toBeThrown))
                    .build()
                    .asExpression();
            return nodeFactory.infixExpression(RIGHT_SHIFT_SIGNED,
                    mockedMethodWithMatchers(mockedMethodInvocation),
                    throwingClosure);
        }
        throw new UnsupportedOperationException("Supported only 1-arity thenThrow invocation");
    }

    private MethodInvocation mockedMethodWithMatchers(MethodInvocation mockedMethodInvocation) {
        return nodeFactory.methodInvocation(mockedMethodInvocation.getName().getFullyQualifiedName(),
                (List<Expression>) mockedMethodInvocation.arguments().stream()
                        .map(matcherHandler::applyMatchers).collect(toList()),
                nodeFactory.clone(mockedMethodInvocation.getExpression()));
    }

    private Expression argumentAsExpression(Object argument) {
        return argument instanceof Expression ? nodeFactory.clone((Expression) argument) :
                nodeFactory.simpleName(argument.toString());
    }
}
