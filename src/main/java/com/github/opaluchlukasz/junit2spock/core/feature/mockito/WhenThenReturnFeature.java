package com.github.opaluchlukasz.junit2spock.core.feature.mockito;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;

public class WhenThenReturnFeature extends MockitoReturnFeature {

    public static final String THEN_RETURN = "thenReturn";
    public static final String WHEN = "when";

    public WhenThenReturnFeature(ASTNodeFactory astNodeFactory, MatcherHandler matcherHandler) {
        super(astNodeFactory, matcherHandler, WHEN, THEN_RETURN);
    }
}
