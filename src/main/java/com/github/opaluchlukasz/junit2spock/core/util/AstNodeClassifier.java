package com.github.opaluchlukasz.junit2spock.core.util;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;

public final class AstNodeClassifier {

    private AstNodeClassifier() {
        // NOOP
    }

    public static boolean isMethodInvocation(Object bodyElement, String methodName) {
        if (bodyElement instanceof ExpressionStatement) {
            Expression expression = ((ExpressionStatement) bodyElement).getExpression();
            if (expression instanceof MethodInvocation) {
                MethodInvocation methodInvocation = ((MethodInvocation) expression);
                if (methodInvocation.getName().getIdentifier().equals(methodName)) {
                    return true;
                }
            }
        } return false;
    }
}
