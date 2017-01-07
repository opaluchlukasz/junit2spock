package com.github.opaluchlukasz.junit2spock.core.visitor;

import com.github.opaluchlukasz.junit2spock.core.model.MethodModel;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodVisitor extends ASTVisitor {

    private MethodModel methodModel;

    @Override
    public boolean visit(MethodDeclaration methodDeclaration) {
        methodModel = new MethodModel(methodDeclaration);
        return true;
    }

    public MethodModel methodModel() {
        return methodModel;
    }

}
