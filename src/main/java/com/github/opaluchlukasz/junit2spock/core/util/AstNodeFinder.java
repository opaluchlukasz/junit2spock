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
        if (bodyElement instanceof MethodInvocation) {
            return methodInvocationOf((MethodInvocation) bodyElement, methodName);
        }
        if (bodyElement instanceof ExpressionStatement) {
            Expression expression = ((ExpressionStatement) bodyElement).getExpression();
            Optional<MethodInvocation> methodInvocation = methodInvocation(expression, methodName);
            if (methodInvocation.isPresent()) {
                return methodInvocation;
            } else if (expression instanceof MethodInvocation) {
                return methodInvocation(((MethodInvocation) expression).getExpression(), methodName);
            }
        }
        if (bodyElement instanceof Expression) {
            return methodInvocationFrom((Expression) bodyElement, methodName);
        }
        return empty();
    }

    private static Optional<MethodInvocation> methodInvocationFrom(Expression expression, String methodName) {
        if (expression instanceof MethodInvocation) {
            return methodInvocationOf(((MethodInvocation) expression), methodName);
        }
        return empty();
    }

    private static Optional<MethodInvocation> methodInvocationOf(MethodInvocation methodInvocation, String methodName) {
        return methodInvocation.getName().getIdentifier().equals(methodName) ? Optional.of(methodInvocation) : empty();
    }
}
