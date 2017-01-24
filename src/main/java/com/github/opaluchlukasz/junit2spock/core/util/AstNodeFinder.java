package com.github.opaluchlukasz.junit2spock.core.util;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.Optional.empty;

public final class AstNodeFinder {

    private AstNodeFinder() {
        // NOOP
    }

    public static Optional<MethodInvocation> methodInvocation(Object bodyElement, String... methodNames) {
        return stream(methodNames)
                .map(methodName -> methodInvocation(bodyElement, methodName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    private static Optional<MethodInvocation> methodInvocation(Object bodyElement, String methodName) {
        if (bodyElement instanceof ExpressionStatement) {
            Expression expression = ((ExpressionStatement) bodyElement).getExpression();
            return methodInvocation(expression, methodName);
        }
        if (bodyElement instanceof Expression) {
            return methodInvocation((Expression) bodyElement, methodName);
        }
        return empty();
    }

    private static Optional<MethodInvocation> methodInvocation(Expression expression, String methodName) {
        if (expression instanceof MethodInvocation) {
            MethodInvocation methodInvocation = ((MethodInvocation) expression);
            if (methodInvocation.getName().getIdentifier().equals(methodName)) {
                return Optional.of(methodInvocation);
            }
        }
        return empty();
    }
}
