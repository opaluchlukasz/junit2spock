package com.github.opaluchlukasz.junit2spock.core.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.SupportedTestFeature;
import com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertEqualsFeature;
import com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertFalseFeature;
import com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertNotNullFeature;
import com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertNullFeature;
import com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertTrueFeature;
import com.github.opaluchlukasz.junit2spock.core.feature.mockito.GivenWillReturnFeature;
import com.github.opaluchlukasz.junit2spock.core.feature.mockito.GivenWillThrowFeature;
import com.github.opaluchlukasz.junit2spock.core.feature.mockito.MatcherHandler;
import com.github.opaluchlukasz.junit2spock.core.feature.mockito.MockAnnotationFeature;
import com.github.opaluchlukasz.junit2spock.core.feature.mockito.MockMethodFeature;
import com.github.opaluchlukasz.junit2spock.core.feature.mockito.MockitoVerifyFeature;
import com.github.opaluchlukasz.junit2spock.core.feature.mockito.MockitoVerifyNoMoreInteractionsFeature;
import com.github.opaluchlukasz.junit2spock.core.feature.mockito.WhenThenReturnFeature;
import com.github.opaluchlukasz.junit2spock.core.feature.mockito.WhenThenThrowFeature;
import com.github.opaluchlukasz.junit2spock.core.node.GroovyClosureBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
final class FeatureFactory {

    private final ASTNodeFactory astNodeFactory;
    private final MatcherHandler matcherHandler;
    private final GroovyClosureBuilder groovyClosureBuilder;

    @Autowired
    FeatureFactory(ASTNodeFactory astNodeFactory,
                   MatcherHandler matcherHandler,
                   GroovyClosureBuilder groovyClosureBuilder) {
        this.astNodeFactory = astNodeFactory;
        this.matcherHandler = matcherHandler;
        this.groovyClosureBuilder = groovyClosureBuilder;
    }

    Feature provide(SupportedTestFeature supportedTestFeatures) {
        switch (supportedTestFeatures) {
            case THEN_RETURN:
                return new WhenThenReturnFeature(astNodeFactory, matcherHandler);
            case WILL_RETURN:
                return new GivenWillReturnFeature(astNodeFactory, matcherHandler);
            case THEN_THROW:
                return new WhenThenThrowFeature(astNodeFactory, matcherHandler, groovyClosureBuilder);
            case WILL_THROW:
                return new GivenWillThrowFeature(astNodeFactory, matcherHandler, groovyClosureBuilder);
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
            case MOCKITO_VERIFY:
                return new MockitoVerifyFeature(astNodeFactory, matcherHandler);
            case MOCKITO_VERIFY_NO_MORE_INTERACTIONS:
                return new MockitoVerifyNoMoreInteractionsFeature(astNodeFactory);
            case MOCK_METHOD:
                return new MockMethodFeature(astNodeFactory);
            default:
                throw new UnsupportedOperationException("Unsupported feature: " + supportedTestFeatures.name());
        }
    }
}
