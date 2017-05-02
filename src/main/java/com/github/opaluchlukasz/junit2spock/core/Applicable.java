package com.github.opaluchlukasz.junit2spock.core;

import com.github.opaluchlukasz.junit2spock.core.feature.Feature;
import com.github.opaluchlukasz.junit2spock.core.feature.FeatureProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

import static java.util.Arrays.stream;

public enum Applicable {
    TEST_METHOD, FIXTURE_METHOD, REGULAR_METHOD, FIELD_FEATURE;

    @Component
    static class ApplicableInjector {
        @Autowired private FeatureProvider featureProvider;

        @PostConstruct
        public void init() {
            stream(Applicable.values()).forEach(applicable -> applicable.featureProvider = featureProvider);
        }
    }

    private FeatureProvider featureProvider;

    public void applyFeaturesToStatements(List<Object> statements) {
        List<Feature> features = features();
        for (int i = 0; i < statements.size(); i++) {
            Object bodyNode = statements.get(i);
            statements.remove(i);
            for (Feature testMethodFeature : features) {
                bodyNode = testMethodFeature.apply(bodyNode);
            }
            statements.add(i, bodyNode);
        }
    }

    public List<Feature> features() {
        return featureProvider.features(this);
    }
}
