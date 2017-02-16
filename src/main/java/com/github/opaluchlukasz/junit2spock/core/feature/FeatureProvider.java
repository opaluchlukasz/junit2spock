package com.github.opaluchlukasz.junit2spock.core.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.Applicable;

import java.util.List;

import static com.github.opaluchlukasz.junit2spock.core.SupportedTestFeature.featuresTypes;
import static java.util.stream.Collectors.toList;

public class FeatureProvider {

    private final ASTNodeFactory astNodeFactory;

    public FeatureProvider(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
    }

    public List<Feature> features(Applicable applicable) {
        return featuresTypes(applicable).stream()
                .map(supported -> FeatureFactory.provide(supported, astNodeFactory))
                .collect(toList());
    }
}
