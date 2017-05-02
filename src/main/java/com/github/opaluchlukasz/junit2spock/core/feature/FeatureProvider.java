package com.github.opaluchlukasz.junit2spock.core.feature;

import com.github.opaluchlukasz.junit2spock.core.Applicable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.github.opaluchlukasz.junit2spock.core.SupportedTestFeature.featuresTypes;
import static java.util.stream.Collectors.toList;

@Component
public class FeatureProvider {

    private final FeatureFactory featureFactory;

    @Autowired
    public FeatureProvider(FeatureFactory featureFactory) {
        this.featureFactory = featureFactory;
    }

    public List<Feature> features(Applicable applicable) {
        return featuresTypes(applicable).stream()
                .map(featureFactory::provide)
                .collect(toList());
    }
}
