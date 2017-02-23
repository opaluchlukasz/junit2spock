package com.github.opaluchlukasz.junit2spock.core.model.method;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.model.method.MethodDeclarationHelper.isPrivate;
import static java.util.Optional.empty;

public class RegularMethodModel extends MethodModel {

    RegularMethodModel(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);
    }

    @Override
    protected String methodSuffix() {
        return "";
    }

    @Override
    protected String getMethodName() {
        return methodDeclaration().getName().toString();
    }

    @Override
    protected Optional<String> methodModifier() {
        if (isPrivate(methodDeclaration())) {
            return Optional.of("private");
        }
        return empty();
    }
}
