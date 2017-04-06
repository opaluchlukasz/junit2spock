package com.github.opaluchlukasz.junit2spock.core.feature.junit;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.feature.Feature;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.EQUALS;

public class AssertEqualsFeature extends Feature<MethodInvocation> {

    public static final String ASSERT_EQUALS = "assertEquals";
    public static final String ASSERT_ARRAY_EQUALS = "assertArrayEquals";

    private final ASTNodeFactory astNodeFactory;

    public AssertEqualsFeature(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
    }

    @Override
    public Optional<MethodInvocation> applicable(Object astNode) {
        return methodInvocation(astNode, ASSERT_EQUALS, ASSERT_ARRAY_EQUALS);
    }

    @Override
    public InfixExpression apply(Object object, MethodInvocation methodInvocation) {
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
        throw new UnsupportedOperationException("Supported only 2-, 3-arity assertEquals/assertArrayEquals invocation");
    }

    private Expression argumentAsExpression(Object argument) {
        return argument instanceof Expression ? astNodeFactory.clone((Expression) argument) :
                astNodeFactory.simpleName(argument.toString());
    }
}
