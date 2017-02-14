package com.github.opaluchlukasz.junit2spock.core.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.List;

import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.EQUALS;

public class AssertNullFeature implements Feature {

    public static final String ASSERT_NULL = "assertNull";

    private final ASTNodeFactory astNodeFactory;

    AssertNullFeature(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
    }

    @Override
    public boolean applicable(Object astNode) {
        return methodInvocation(astNode, ASSERT_NULL).isPresent();
    }

    @Override
    public InfixExpression apply(Object object) {
        MethodInvocation methodInvocation = methodInvocation(object, ASSERT_NULL).get();
        List arguments = methodInvocation.arguments();
        if (arguments.size() == 1) {
            return astNodeFactory.infixExpression(EQUALS,
                    argumentAsExpression(arguments.get(0)),
                    astNodeFactory.nullLiteral());
        }
        if (arguments.size() == 2) {
            return astNodeFactory.infixExpression(EQUALS,
                    argumentAsExpression(arguments.get(1)),
                    astNodeFactory.nullLiteral());
        }
        throw new UnsupportedOperationException("Supported only 1-, 2-arity assertNull invocation");
    }

    private Expression argumentAsExpression(Object argument) {
        return argument instanceof Expression ? astNodeFactory.clone((Expression) argument) :
                astNodeFactory.simpleName(argument.toString());
    }
}
