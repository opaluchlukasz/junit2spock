package com.github.opaluchlukasz.junit2spock.core.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.node.SpockMockBehaviour;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;

public class ThenThrowFeature extends Feature<MethodInvocation> {

    public static final String THEN_THROW = "thenThrow";
    public static final String WHEN = "when";

    private final ASTNodeFactory astNodeFactory;

    ThenThrowFeature(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
    }

    @Override
    public Optional<MethodInvocation> applicable(Object astNode) {
        return methodInvocation(astNode, THEN_THROW)
                .flatMap(thenReturnInvocation -> methodInvocation(thenReturnInvocation.getExpression(), WHEN))
                .filter(whenInvocation -> whenInvocation.arguments().size() == 1);
    }

    @Override
    public SpockMockBehaviour apply(Object object, MethodInvocation whenMethodInvocation) {
        MethodInvocation methodInvocation = methodInvocation(object, THEN_THROW).get();
        List arguments = methodInvocation.arguments();
        if (arguments.size() == 1) {
            Expression toBeThrown = argumentAsExpression(arguments.get(0));
            Block closure = astNodeFactory.block();
            closure.statements().add(astNodeFactory.throwStatement(toBeThrown));
            return new SpockMockBehaviour((MethodInvocation) whenMethodInvocation.arguments().get(0), closure);
        }
        throw new UnsupportedOperationException("Supported only 1-arity thenThrow invocation");
    }

    private Expression argumentAsExpression(Object argument) {
        return argument instanceof Expression ? astNodeFactory.clone((Expression) argument) :
                astNodeFactory.simpleName(argument.toString());
    }
}
