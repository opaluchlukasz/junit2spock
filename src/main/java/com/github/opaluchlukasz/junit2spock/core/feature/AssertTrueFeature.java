package com.github.opaluchlukasz.junit2spock.core.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.List;

import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;

public class AssertTrueFeature implements Feature {

    public static final String ASSERT_TRUE = "assertTrue";

    private final ASTNodeFactory astNodeFactory;

    AssertTrueFeature(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
    }

    @Override
    public boolean applicable(Object astNode) {
        return methodInvocation(astNode, ASSERT_TRUE).isPresent();
    }

    @Override
    public Expression apply(Object object) {
        MethodInvocation methodInvocation = methodInvocation(object, ASSERT_TRUE).get();
        List arguments = methodInvocation.arguments();
        if (arguments.size() == 1) {
            return argumentAsExpression(arguments.get(0));
        }
        if (arguments.size() == 2) {
            return argumentAsExpression(arguments.get(1));
        }
        throw new UnsupportedOperationException("Supported only 1-, 2-arity assertTrue invocation");
    }

    private Expression argumentAsExpression(Object argument) {
        return argument instanceof Expression ? astNodeFactory.clone((Expression) argument) :
                astNodeFactory.simpleName(argument.toString());
    }
}
