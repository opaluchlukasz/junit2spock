package com.github.opaluchlukasz.junit2spock.core.model.method;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.Applicable.FIXTURE_METHOD;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;

public class FixtureMethodModel extends MethodModel {

    private final String fixtureMethodName;

    FixtureMethodModel(MethodDeclaration methodDeclaration, String fixtureMethodName) {
        super(methodDeclaration);
        this.fixtureMethodName = fixtureMethodName;
        applyFeaturesToMethodBody(FIXTURE_METHOD);
    }

    @Override
    protected String methodSuffix() {
        return SEPARATOR;
    }

    @Override
    protected String getMethodName() {
        return fixtureMethodName;
    }

    @Override
    protected Optional<String> methodModifier() {
        return Optional.of("def");
    }
}
