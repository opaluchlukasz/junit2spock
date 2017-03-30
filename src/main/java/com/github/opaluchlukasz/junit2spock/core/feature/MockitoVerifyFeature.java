package com.github.opaluchlukasz.junit2spock.core.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;
import static java.util.stream.Collectors.toList;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.TIMES;

public class MockitoVerifyFeature extends Feature<MethodInvocation> {

    public static final String VERIFY = "verify";

    private final ASTNodeFactory astNodeFactory;

    MockitoVerifyFeature(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
    }

    @Override
    public Optional<MethodInvocation> applicable(Object astNode) {
        return Optional.of(astNode)
                .filter(statement -> statement instanceof ExpressionStatement)
                .map(statement -> ((ExpressionStatement) statement).getExpression())
                .filter(expression -> expression instanceof MethodInvocation)
                .map(expression -> ((MethodInvocation) expression).getExpression())
                .flatMap(expression -> methodInvocation(expression, VERIFY))
                .filter(invocation -> invocation.arguments().size() == 1);
    }

    @Override
    public InfixExpression apply(Object object, MethodInvocation verifyMethodInvocation) {
        List arguments = verifyMethodInvocation.arguments();
        MethodInvocation parentMethodInvocation = (MethodInvocation) verifyMethodInvocation.getParent();
        if (arguments.size() == 1) {
            return astNodeFactory.infixExpression(TIMES,
                    astNodeFactory.simpleName("_"),
                    astNodeFactory.methodInvocation(parentMethodInvocation.getName().getFullyQualifiedName(),
                            (List<ASTNode>) parentMethodInvocation.arguments().stream()
                                    .map(astNodeFactory::clone).collect(toList()),
                            argumentAsExpression(arguments.get(0))));
        }
        throw new UnsupportedOperationException("Supported only 1-arity verify invocation");
    }

    private Expression argumentAsExpression(Object argument) {
        return argument instanceof Expression ? astNodeFactory.clone((Expression) argument) :
                astNodeFactory.simpleName(argument.toString());
    }
}
