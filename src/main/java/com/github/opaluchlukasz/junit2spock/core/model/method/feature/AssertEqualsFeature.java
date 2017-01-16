package com.github.opaluchlukasz.junit2spock.core.model.method.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.List;

import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeClassifier.isMethodInvocation;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.EQUALS;

public class AssertEqualsFeature implements TestMethodFeature {

    static final String ASSERT_EQUALS = "assertEquals";

    private final ASTNodeFactory astNodeFactory;

    AssertEqualsFeature(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
    }

    @Override
    public boolean applicable(Object astNode) {
        return isMethodInvocation(astNode, ASSERT_EQUALS);
    }

    @Override
    public InfixExpression apply(Object object) {
        MethodInvocation methodInvocation = ((MethodInvocation)((ExpressionStatement) object).getExpression());
        List arguments = methodInvocation.arguments();
        if (arguments.size() == 2) {
            return astNodeFactory.infixExpression(EQUALS,
                    argumentAsExpression(arguments.get(1)),
                    argumentAsExpression(arguments.get(0)));
        }
        if (arguments.size() == 3) {
            return astNodeFactory.infixExpression(EQUALS,
                    argumentAsExpression(arguments.get(2)),
                    argumentAsExpression(arguments.get(1)));
        }
        throw new UnsupportedOperationException("Supported only 2-, 3-arity assertEquals invocation");
    }

    private Expression argumentAsExpression(Object argument) {
        return argument instanceof Expression ? astNodeFactory.clone(argument) :
                astNodeFactory.simpleName(argument.toString());
    }
}
