package com.github.opaluchlukasz.junit2spock.core.util;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.Optional;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
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

    public static Optional<MethodInvocation> methodInvocation(Object bodyElement) {
        return Match(bodyElement).of(
                Case($(instanceOf(MethodInvocation.class)), Optional::of),
                Case($(instanceOf(ExpressionStatement.class)), stmt -> {
                    Expression expression = stmt.getExpression();
                    Optional<MethodInvocation> methodInvocation = methodInvocation(expression);
                    if (methodInvocation.isPresent()) {
                        return methodInvocation;
                    } else if (expression instanceof MethodInvocation) {
                        return methodInvocation(((MethodInvocation) expression).getExpression());
                    }
                    return empty();
                }),
                Case($(), empty())
        );
    }

    private static Optional<MethodInvocation> methodInvocation(Object bodyElement, String methodName) {
        return Match(bodyElement).of(
                Case($(instanceOf(MethodInvocation.class)), stmt -> methodInvocationOf(stmt, methodName)),
                Case($(instanceOf(ExpressionStatement.class)), stmt -> handleExpressionStatement(stmt, methodName)),
                Case($(instanceOf(Expression.class)), stmt -> methodInvocationFrom(stmt, methodName)),
                Case($(), empty())
        );
    }

    private static Optional<MethodInvocation> handleExpressionStatement(ExpressionStatement stmt, String methodName) {
        Expression expression = stmt.getExpression();
        Optional<MethodInvocation> methodInvocation = methodInvocation(expression, methodName);
        if (methodInvocation.isPresent()) {
            return methodInvocation;
        } else if (expression instanceof MethodInvocation) {
            return methodInvocation(((MethodInvocation) expression).getExpression(), methodName);
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
