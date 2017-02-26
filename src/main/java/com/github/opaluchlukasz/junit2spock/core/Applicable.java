package com.github.opaluchlukasz.junit2spock.core;

import com.github.opaluchlukasz.junit2spock.core.feature.Feature;
import com.github.opaluchlukasz.junit2spock.core.feature.FeatureProvider;

import java.util.List;

public enum Applicable {
    TEST_METHOD, FIXTURE_METHOD, REGULAR_METHOD, FIELD_FEATURE;

    public void applyFeaturesToStatements(List<Object> statements, ASTNodeFactory astNodeFactory) {
        List<Feature> features = new FeatureProvider(astNodeFactory).features(this);
        for (int i = 0; i < statements.size(); i++) {
            Object bodyNode = statements.get(i);
            statements.remove(bodyNode);
            for (Feature testMethodFeature : features) {
                bodyNode = testMethodFeature.apply(bodyNode);
            }
            statements.add(i, bodyNode);
        }
    }
}
