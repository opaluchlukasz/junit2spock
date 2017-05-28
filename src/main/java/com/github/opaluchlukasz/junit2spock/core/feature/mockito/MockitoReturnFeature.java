package com.github.opaluchlukasz.junit2spock.core.feature.mockito;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.feature.Feature;
import com.github.opaluchlukasz.junit2spock.core.node.SpockMockReturnSequences;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;
import static java.util.stream.Collectors.toList;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.RIGHT_SHIFT_SIGNED;

public class MockitoReturnFeature extends Feature<MethodInvocation> {

    private final ASTNodeFactory nodeFactory;
    private final MatcherHandler matcherHandler;
    private final String when;
    private final String thenReturn;

    public MockitoReturnFeature(ASTNodeFactory nodeFactory, MatcherHandler matcherHandler, String when, String thenReturn) {
        this.nodeFactory = nodeFactory;
        this.matcherHandler = matcherHandler;
        this.when = when;
        this.thenReturn = thenReturn;
    }

    @Override
    public Optional<MethodInvocation> applicable(Object astNode) {
        return methodInvocation(astNode, thenReturn)
                .flatMap(thenReturnInvocation -> methodInvocation(thenReturnInvocation.getExpression(), when))
                .filter(whenInvocation -> whenInvocation.arguments().size() == 1);
    }

    @Override
    public Object apply(Object object, MethodInvocation whenMethodInvocation) {
        MethodInvocation methodInvocation = methodInvocation(object, thenReturn).get();
        MethodInvocation mockedMethodInvocation = (MethodInvocation) whenMethodInvocation.arguments().get(0);
        List arguments = methodInvocation.arguments();
        if (arguments.size() == 1) {
            return nodeFactory.infixExpression(RIGHT_SHIFT_SIGNED,
                    mockedMethodWithMatchers(mockedMethodInvocation),
                    argumentAsExpression(arguments.get(0)));
        } else {
            return new SpockMockReturnSequences(mockedMethodWithMatchers(mockedMethodInvocation),
                    (List<Object>) arguments.stream().map(this::argumentAsExpression).collect(toList()));
        }
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
