package com.github.opaluchlukasz.junit2spock.core.feature.junit;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.feature.Feature;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;
import static org.eclipse.jdt.core.dom.PrefixExpression.Operator.NOT;

public class AssertFalseFeature extends Feature<MethodInvocation> {

    public static final String ASSERT_FALSE = "assertFalse";

    private final ASTNodeFactory astNodeFactory;

    public AssertFalseFeature(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
    }

    @Override
    public Optional<MethodInvocation> applicable(Object astNode) {
        return methodInvocation(astNode, ASSERT_FALSE);
    }

    @Override
    public Expression apply(Object object, MethodInvocation methodInvocation) {
        List arguments = methodInvocation.arguments();
        if (arguments.size() == 1) {
            return astNodeFactory.prefixExpression(NOT, argumentAsExpression(arguments.get(0)));
        }
        if (arguments.size() == 2) {
            return astNodeFactory.prefixExpression(NOT, argumentAsExpression(arguments.get(1)));
        }
        throw new UnsupportedOperationException("Supported only 1-, 2-arity assertFalse invocation");
    }

    private Expression argumentAsExpression(Object argument) {
        return argument instanceof Expression ? astNodeFactory.clone((Expression) argument) :
                astNodeFactory.simpleName(argument.toString());
    }
}
