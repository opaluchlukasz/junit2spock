package com.github.opaluchlukasz.junit2spock.core.model.method.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.SupportedJunitFeatures;

final class TestMethodFeatureFactory {

    private TestMethodFeatureFactory() {
        // NOOP
    }

    static TestMethodFeature provide(SupportedJunitFeatures supportedJunitFeatures, ASTNodeFactory astNodeFactory) {
        switch (supportedJunitFeatures) {
            case ASSERT_EQUALS:
                return new AssertEqualsFeature(astNodeFactory);
            case ASSERT_NOT_NULL:
                return new AssertNotNullFeature(astNodeFactory);
            default:
                throw new UnsupportedOperationException("Unsupported test method feature: " + supportedJunitFeatures.name());
        }
    }
}
