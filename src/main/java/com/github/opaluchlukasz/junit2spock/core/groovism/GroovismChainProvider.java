package com.github.opaluchlukasz.junit2spock.core.groovism;

public final class GroovismChainProvider {

    private GroovismChainProvider() {
        //NOOP
    }

    public static Groovism provide() {
        return new NoSemicolon(new NoClassKeyword(new RegularString()));
    }
}
