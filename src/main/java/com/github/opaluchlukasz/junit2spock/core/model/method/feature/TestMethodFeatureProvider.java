package com.github.opaluchlukasz.junit2spock.core.model.method.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.SupportedJunitFeatures;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class TestMethodFeatureProvider {

    private final ASTNodeFactory astNodeFactory;

    public TestMethodFeatureProvider(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
    }

    public List<TestMethodFeature> testMethodFeatures() {
        return SupportedJunitFeatures.testMethodFeatures().stream()
                .map(supported -> TestMethodFeatureFactory.provide(supported, astNodeFactory))
                .collect(toList());
    }
}
