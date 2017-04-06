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

    private final ASTNodeFactory astNodeFactory;
    private final String when;
    private final String thenReturn;

    public MockitoReturnFeature(ASTNodeFactory astNodeFactory, String when, String thenReturn) {
        this.astNodeFactory = astNodeFactory;
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
        List arguments = methodInvocation.arguments();
        if (arguments.size() == 1) {
            return astNodeFactory.infixExpression(RIGHT_SHIFT_SIGNED,
                    argumentAsExpression(whenMethodInvocation.arguments().get(0)),
                    argumentAsExpression(arguments.get(0)));
        } else {
            return new SpockMockReturnSequences(argumentAsExpression(whenMethodInvocation.arguments().get(0)),
                    (List<Object>) arguments.stream().map(this::argumentAsExpression).collect(toList()));
        }
    }

    private Expression argumentAsExpression(Object argument) {
        return argument instanceof Expression ? astNodeFactory.clone((Expression) argument) :
                astNodeFactory.simpleName(argument.toString());
    }
}
