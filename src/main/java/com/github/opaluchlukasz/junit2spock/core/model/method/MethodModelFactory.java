package com.github.opaluchlukasz.junit2spock.core.model.method;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import static com.github.opaluchlukasz.junit2spock.core.model.method.MethodDeclarationHelper.isTestMethod;

public final class MethodModelFactory {

    private MethodModelFactory() {
        // NOOP
    }

    public static MethodModel get(MethodDeclaration methodDeclaration) {
        if (isTestMethod(methodDeclaration)) {
            return new TestMethodModel(methodDeclaration);
        } else {
            return new RegularMethodModel(methodDeclaration);
        }
    }
}
