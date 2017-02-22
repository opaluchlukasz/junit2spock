package com.github.opaluchlukasz.junit2spock.core.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;

public class WhenThenReturnFeature extends MockitoReturnFeature {

    public static final String THEN_RETURN = "thenReturn";
    public static final String WHEN = "when";

    WhenThenReturnFeature(ASTNodeFactory astNodeFactory) {
        super(astNodeFactory, WHEN, THEN_RETURN);
    }
}
