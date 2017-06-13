package com.github.opaluchlukasz.junit2spock.core.feature.mockito;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.node.GroovyClosureBuilder;

public class WhenThenThrowFeature extends MockitoThrowFeature {

    public static final String THEN_THROW = "thenThrow";
    public static final String WHEN = "when";

    public WhenThenThrowFeature(ASTNodeFactory nodeFactory, MatcherHandler matcherHandler,
                                GroovyClosureBuilder groovyClosureBuilder) {
        super(nodeFactory, matcherHandler, groovyClosureBuilder, WHEN, THEN_THROW);
    }
}
