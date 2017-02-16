package com.github.opaluchlukasz.junit2spock.core.model.method;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.Applicable.FIXTURE_METHOD;

public class FixtureMethodModel extends MethodModel {

    private final List<Object> body = new LinkedList<>();
    private final String fixtureMethodName;

    FixtureMethodModel(MethodDeclaration methodDeclaration, String fixtureMethodName) {
        super(methodDeclaration);
        this.fixtureMethodName = fixtureMethodName;
        if (methodDeclaration.getBody() != null && methodDeclaration.getBody().statements() != null) {
            this.body.addAll(methodDeclaration.getBody().statements());
        }
        applyFeaturesToMethodBody(FIXTURE_METHOD);
    }

    @Override
    protected String methodSuffix() {
        return "";
    }

    @Override
    protected String getMethodName() {
        return fixtureMethodName;
    }

    @Override
    protected List<Object> body() {
        return body;
    }

    @Override
    protected Optional<String> methodModifier() {
        return Optional.of("def");
    }
}
