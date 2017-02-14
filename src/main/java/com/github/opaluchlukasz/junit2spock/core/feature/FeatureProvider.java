package com.github.opaluchlukasz.junit2spock.core.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;

import java.util.List;

import static com.github.opaluchlukasz.junit2spock.core.Applicable.FIELD_FEATURE;
import static com.github.opaluchlukasz.junit2spock.core.Applicable.TEST_METHOD;
import static com.github.opaluchlukasz.junit2spock.core.SupportedTestFeature.features;
import static java.util.stream.Collectors.toList;

public class FeatureProvider {

    private final ASTNodeFactory astNodeFactory;

    public FeatureProvider(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
    }

    public List<Feature> testMethodFeatures() {
        return features(TEST_METHOD).stream()
                .map(supported -> FeatureFactory.provide(supported, astNodeFactory))
                .collect(toList());
    }

    public List<Feature> fieldFeatures() {
        return features(FIELD_FEATURE).stream()
                .map(supported -> FeatureFactory.provide(supported, astNodeFactory))
                .collect(toList());
    }
}
