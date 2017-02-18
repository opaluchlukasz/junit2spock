package com.github.opaluchlukasz.junit2spock.core.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.SupportedTestFeature;

final class FeatureFactory {

    private FeatureFactory() {
        //NOOP
    }

    static Feature provide(SupportedTestFeature supportedTestFeatures, ASTNodeFactory astNodeFactory) {
        switch (supportedTestFeatures) {
            case THEN_RETURN:
                return new ThenReturnFeature(astNodeFactory);
            case THEN_THROW:
                return new ThenThrowFeature(astNodeFactory);
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
            case MOCK_ANNOTATION:
                return new MockAnnotationFeature(astNodeFactory);
            case MOCK_METHOD:
                return new MockMethodFeature(astNodeFactory);
            default:
                throw new UnsupportedOperationException("Unsupported feature: " + supportedTestFeatures.name());
        }
    }
}
