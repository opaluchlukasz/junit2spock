package com.github.opaluchlukasz.junit2spock.core.model.method.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.SupportedTestFeatures;

final class TestMethodFeatureFactory {

    private TestMethodFeatureFactory() {
        //NOOP
    }

    static TestMethodFeature provide(SupportedTestFeatures supportedTestFeatures, ASTNodeFactory astNodeFactory) {
        switch (supportedTestFeatures) {
            case THEN_RETURN:
                return new ThenReturnFeature(astNodeFactory);
            case ASSERT_EQUALS:
                return new AssertEqualsFeature(astNodeFactory);
            case ASSERT_NOT_NULL:
                return new AssertNotNullFeature(astNodeFactory);
            case ASSERT_NULL:
                return new AssertNullFeature(astNodeFactory);
            case ASSERT_TRUE:
                return new AssertTrueFeature(astNodeFactory);
            case ASSERT_FALSE:
                return new AssertFalseFeature(astNodeFactory);
            default:
                throw new UnsupportedOperationException("Unsupported test method feature: " + supportedTestFeatures.name());
        }
    }
}
