package com.github.opaluchlukasz.junit2spock.core.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.TIMES;

public class MockitoVerifyNoMoreInteractionsFeature extends Feature<MethodInvocation> {

    public static final String VERIFY_NO_MORE_INTERACTIONS = "verifyNoMoreInteractions";

    private final ASTNodeFactory nodeFactory;

    MockitoVerifyNoMoreInteractionsFeature(ASTNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    @Override
    public Optional<MethodInvocation> applicable(Object astNode) {
        return methodInvocation(astNode, VERIFY_NO_MORE_INTERACTIONS)
                .filter(invocation -> invocation.arguments().size() == 1);
    }

    @Override
    public InfixExpression apply(Object object, MethodInvocation verifyMethodInvocation) {
        List arguments = verifyMethodInvocation.arguments();
        return nodeFactory.infixExpression(TIMES,
                nodeFactory.numberLiteral("0"),
                nodeFactory.fieldAccess("_", (Expression) nodeFactory.clone(arguments.get(0))));
    }
}
