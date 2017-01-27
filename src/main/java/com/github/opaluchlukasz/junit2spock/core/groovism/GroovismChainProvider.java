package com.github.opaluchlukasz.junit2spock.core.groovism;

import static java.util.Optional.empty;

public class GroovismChainProvider {

    public static Groovism provide() {
        return new NoSemicolon(empty());
    }
}
