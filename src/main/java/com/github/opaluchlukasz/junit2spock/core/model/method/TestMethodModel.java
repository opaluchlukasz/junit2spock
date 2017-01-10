package com.github.opaluchlukasz.junit2spock.core.model.method;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.LinkedList;
import java.util.List;

import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.expect;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.given;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.then;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.when;

public class TestMethodModel extends MethodModel {

    private final List<Object> body = new LinkedList<>();

    TestMethodModel(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);
        if (methodDeclaration.getBody() != null && methodDeclaration.getBody().statements() != null) {
            body.addAll(methodDeclaration.getBody().statements());
        }
        addSpockSpecificBlocksToBody();
    }

    private void addSpockSpecificBlocksToBody() {
        int thenIndex = thenExpectBlockStart();
        boolean then = isWhenThenStrategy(thenIndex);

        if (then) {
            body.add(thenIndex, then());
            body.add(thenIndex - 1, when());
            if (thenIndex - 2 >= 0) {
                body.add(0, given());
            }
        } else {
            body.add(thenIndex, expect());
            if (thenIndex - 1 >= 0) {
                body.add(0, given());
            }
        }
    }

    private boolean isWhenThenStrategy(int index) {
        if (index == 0) {
            return false;
        } else {
            if (body.get(index - 1) instanceof ExpressionStatement) {
                Expression expression = ((ExpressionStatement) body.get(index - 1)).getExpression();
                if (expression instanceof MethodInvocation) {
                    return true;
                }
            }
        }
        return false;
    }

    private int thenExpectBlockStart() {
        for (int i = 0; i < body.size(); i++) {
            if (body.get(i) instanceof ExpressionStatement) {
                Expression expression = ((ExpressionStatement) body.get(i)).getExpression();
                if (expression instanceof MethodInvocation) {
                    MethodInvocation methodInvocation = ((MethodInvocation) expression);
                    if (methodInvocation.getName().getIdentifier().equals("assertEquals")) {
                        return i;
                    }
                }
            }
        }
        return 0;
    }

    @Override
    protected List<Object> body() {
        return body;
    }

    @Override
    protected String methodModifier() {
        return "def ";
    }
}
