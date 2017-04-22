package com.github.opaluchlukasz.junit2spock.core.model.method;

import com.github.opaluchlukasz.junit2spock.core.Applicable;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import static com.github.opaluchlukasz.junit2spock.core.Applicable.FIXTURE_METHOD;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;

public class FixtureMethodModel extends MethodModel {

    private final String fixtureMethodName;

    FixtureMethodModel(MethodDeclaration methodDeclaration, String fixtureMethodName) {
        super(methodDeclaration);
        this.fixtureMethodName = fixtureMethodName;
        methodType().applyFeaturesToStatements(body(), astNodeFactory());
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
    protected Applicable methodType() {
        return FIXTURE_METHOD;
    }

    @Override
    protected String methodModifier() {
        return "def ";
    }
}
