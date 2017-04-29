package com.github.opaluchlukasz.junit2spock.core.model.method;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.github.opaluchlukasz.junit2spock.core.model.method.MethodDeclarationHelper.annotatedWith;
import static com.github.opaluchlukasz.junit2spock.core.model.method.MethodDeclarationHelper.isTestMethod;

@Component
public class MethodModelFactory {

    private final ASTNodeFactory nodeFactory;

    @Autowired
    public MethodModelFactory(ASTNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public MethodModel get(MethodDeclaration methodDeclaration) {
        if (isTestMethod(methodDeclaration)) {
            return new TestMethodModel(nodeFactory, methodDeclaration);
        } else if (annotatedWith(methodDeclaration, "Before").isPresent()) {
            return new FixtureMethodModel(nodeFactory, methodDeclaration, "setup");
        } else if (annotatedWith(methodDeclaration, "BeforeClass").isPresent()) {
            return new FixtureMethodModel(nodeFactory, methodDeclaration, "setupSpec");
        } else if (annotatedWith(methodDeclaration, "After").isPresent()) {
            return new FixtureMethodModel(nodeFactory, methodDeclaration, "cleanup");
        } else if (annotatedWith(methodDeclaration, "AfterClass").isPresent()) {
            return new FixtureMethodModel(nodeFactory, methodDeclaration, "cleanupSpec");
        } else {
            return new RegularMethodModel(nodeFactory, methodDeclaration);
        }
    }
}
