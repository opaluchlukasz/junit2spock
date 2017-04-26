package com.github.opaluchlukasz.junit2spock.core.node;

import org.eclipse.jdt.core.dom.InfixExpression.Operator;

import java.lang.reflect.Constructor;

public final class CustomInfixOperator {

    public static final Operator RANGE = getOperator("..");
    public static final Operator CAST = getOperator("as");

    private CustomInfixOperator() {
        //NOOP
    }

    private static Operator getOperator(String operator) {
        try {
            Constructor<Operator> constructor = Operator.class.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(operator);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create instance of the operator", e);
        }
    }
}
