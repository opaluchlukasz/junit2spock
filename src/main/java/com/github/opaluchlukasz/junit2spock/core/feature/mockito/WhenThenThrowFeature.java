package com.github.opaluchlukasz.junit2spock.core.feature.mockito;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.node.GroovyClosureFactory;

public class WhenThenThrowFeature extends MockitoThrowFeature {

    public static final String THEN_THROW = "thenThrow";
    public static final String WHEN = "when";

    public WhenThenThrowFeature(ASTNodeFactory nodeFactory, MatcherHandler matcherHandler,
                                GroovyClosureFactory groovyClosureFactory) {
        super(nodeFactory, matcherHandler, groovyClosureFactory, WHEN, THEN_THROW);
    }
}
