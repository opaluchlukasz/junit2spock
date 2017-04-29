package com.github.opaluchlukasz.junit2spock.core.visitor;

import com.github.opaluchlukasz.junit2spock.core.model.method.MethodModel;
import com.github.opaluchlukasz.junit2spock.core.model.method.MethodModelFactory;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodVisitor extends ASTVisitor {

    private final MethodModelFactory methodModelFactory;
    private MethodModel methodModel;

    MethodVisitor(MethodModelFactory methodModelFactory) {
        this.methodModelFactory = methodModelFactory;
    }

    @Override
    public boolean visit(MethodDeclaration methodDeclaration) {
        methodModel = methodModelFactory.get(methodDeclaration);
        return true;
    }

    public MethodModel methodModel() {
        return methodModel;
    }
}
