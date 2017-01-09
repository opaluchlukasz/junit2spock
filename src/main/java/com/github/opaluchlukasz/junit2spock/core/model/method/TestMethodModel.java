package com.github.opaluchlukasz.junit2spock.core.model.method;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.LinkedList;
import java.util.List;

import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.expect;

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
        if (body.size() == 1) {
            body.add(0, expect());
        }
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
