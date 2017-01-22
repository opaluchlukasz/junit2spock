package com.github.opaluchlukasz.junit2spock.core.util;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;

import static java.util.Arrays.stream;

public final class AstNodeClassifier {

    private AstNodeClassifier() {
        // NOOP
    }

    public static boolean isMethodInvocation(Object bodyElement, String... methodNames) {
        return stream(methodNames)
                .anyMatch(methodName -> isMethodInvocation(bodyElement, methodName));
    }

    private static boolean isMethodInvocation(Object bodyElement, String methodName) {
        if (bodyElement instanceof ExpressionStatement) {
            Expression expression = ((ExpressionStatement) bodyElement).getExpression();
            if (isMethodInvocation(expression, methodName)) {
                return true;
            }
        }
        if (bodyElement instanceof Expression) {
            if (isMethodInvocation(((Expression) bodyElement), methodName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isMethodInvocation(Expression expression, String methodName) {
        if (expression instanceof MethodInvocation) {
            MethodInvocation methodInvocation = ((MethodInvocation) expression);
            if (methodInvocation.getName().getIdentifier().equals(methodName)) {
                return true;
            }
        }
        return false;
    }
}
