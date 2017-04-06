package com.github.opaluchlukasz.junit2spock.core.feature.mockito;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;

public class GivenWillReturnFeature extends MockitoReturnFeature {

    public static final String WILL_RETURN = "willReturn";
    public static final String GIVEN = "given";

    public GivenWillReturnFeature(ASTNodeFactory astNodeFactory) {
        super(astNodeFactory, GIVEN, WILL_RETURN);
    }
}
