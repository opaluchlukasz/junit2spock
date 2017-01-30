package com.github.opaluchlukasz.junit2spock.core.groovism;

import static java.util.Optional.empty;

public final class GroovismChainProvider {

    private GroovismChainProvider() {
        //NOOP
    }

    public static Groovism provide() {
        return new NoSemicolon(empty());
    }
}
