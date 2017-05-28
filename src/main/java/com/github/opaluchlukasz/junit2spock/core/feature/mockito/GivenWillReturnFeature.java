package com.github.opaluchlukasz.junit2spock.core.feature.mockito;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;

public class GivenWillReturnFeature extends MockitoReturnFeature {

    public static final String WILL_RETURN = "willReturn";
    private static final String GIVEN = "given";

    public GivenWillReturnFeature(ASTNodeFactory astNodeFactory, MatcherHandler matcherHandler) {
        super(astNodeFactory, matcherHandler, GIVEN, WILL_RETURN);
    }
}
