package com.github.opaluchlukasz.junit2spock.core.feature

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import spock.lang.Shared
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.SupportedTestFeature.TEST_ANNOTATION
import static com.github.opaluchlukasz.junit2spock.core.feature.FeatureFactory.provide

class TestMethodFeatureFactoryTest extends Specification {

    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory()

    def 'should throw an exception for unsupported test feature'() {
        when:
        provide(TEST_ANNOTATION, nodeFactory)

        then:
        UnsupportedOperationException ex = thrown()
        ex.message == "Unsupported feature: ${TEST_ANNOTATION.name()}"
    }
}
