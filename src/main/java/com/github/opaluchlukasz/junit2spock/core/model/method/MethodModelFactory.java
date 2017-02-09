package com.github.opaluchlukasz.junit2spock.core.model.method;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import static com.github.opaluchlukasz.junit2spock.core.model.method.MethodDeclarationHelper.annotatedWith;
import static com.github.opaluchlukasz.junit2spock.core.model.method.MethodDeclarationHelper.isTestMethod;

public final class MethodModelFactory {

    private MethodModelFactory() {
        // NOOP
    }

    public static MethodModel get(MethodDeclaration methodDeclaration) {
        if (isTestMethod(methodDeclaration)) {
            return new TestMethodModel(methodDeclaration);
        } else if (annotatedWith(methodDeclaration, "Before").isPresent()) {
            return new FixtureMethodModel(methodDeclaration, "setup");
        } else if (annotatedWith(methodDeclaration, "BeforeClass").isPresent()) {
            return new FixtureMethodModel(methodDeclaration, "setupSpec");
        } else if (annotatedWith(methodDeclaration, "After").isPresent()) {
            return new FixtureMethodModel(methodDeclaration, "cleanup");
        } else if (annotatedWith(methodDeclaration, "AfterClass").isPresent()) {
            return new FixtureMethodModel(methodDeclaration, "cleanupSpec");
        } else {
            return new RegularMethodModel(methodDeclaration);
        }
    }
}
