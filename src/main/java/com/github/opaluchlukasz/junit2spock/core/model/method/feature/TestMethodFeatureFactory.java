package com.github.opaluchlukasz.junit2spock.core.model.method.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.Supported;

final class TestMethodFeatureFactory {

    private TestMethodFeatureFactory() {
        // NOOP
    }

    static TestMethodFeature provide(Supported supported, ASTNodeFactory astNodeFactory) {
        switch (supported) {
            case ASSERT_EQUALS:
                return new AssertEqualsFeature(astNodeFactory);
            default:
                throw new UnsupportedOperationException("Unsupported test method feature: " + supported.name());
        }
    }
}
