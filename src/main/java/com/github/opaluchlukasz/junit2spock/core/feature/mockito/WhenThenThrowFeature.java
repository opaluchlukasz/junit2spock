package com.github.opaluchlukasz.junit2spock.core.feature.mockito;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.feature.Feature;
import com.github.opaluchlukasz.junit2spock.core.node.GroovyClosureFactory;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.RIGHT_SHIFT_SIGNED;

public class WhenThenThrowFeature extends Feature<MethodInvocation> {

    public static final String THEN_THROW = "thenThrow";
    public static final String WHEN = "when";

    private final ASTNodeFactory nodeFactory;
    private final GroovyClosureFactory groovyClosureFactory;

    public WhenThenThrowFeature(ASTNodeFactory nodeFactory, GroovyClosureFactory groovyClosureFactory) {
        this.nodeFactory = nodeFactory;
        this.groovyClosureFactory = groovyClosureFactory;
    }

    @Override
    public Optional<MethodInvocation> applicable(Object astNode) {
        return methodInvocation(astNode, THEN_THROW)
                .flatMap(thenReturnInvocation -> methodInvocation(thenReturnInvocation.getExpression(), WHEN))
                .filter(whenInvocation -> whenInvocation.arguments().size() == 1);
    }

    @Override
    public Expression apply(Object object, MethodInvocation whenMethodInvocation) {
        MethodInvocation methodInvocation = methodInvocation(object, THEN_THROW).get();
        List arguments = methodInvocation.arguments();
        if (arguments.size() == 1) {
            Expression toBeThrown = argumentAsExpression(arguments.get(0));
            Block closure = nodeFactory.block(nodeFactory.throwStatement(toBeThrown));
            Expression throwingClosure = groovyClosureFactory.create(closure);
            return nodeFactory.infixExpression(RIGHT_SHIFT_SIGNED,
                    argumentAsExpression(whenMethodInvocation.arguments().get(0)),
                    throwingClosure);
        }
        throw new UnsupportedOperationException("Supported only 1-arity thenThrow invocation");
    }

    private Expression argumentAsExpression(Object argument) {
        return argument instanceof Expression ? nodeFactory.clone((Expression) argument) :
                nodeFactory.simpleName(argument.toString());
    }
}
