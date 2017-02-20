package com.github.opaluchlukasz.junit2spock.core.groovism;

import java.util.Optional;

import static java.util.Optional.empty;

public class RegularString extends Groovism {

    RegularString() {
        super(empty());
    }

    RegularString(Groovism next) {
        super(Optional.of(next));
    }

    @Override
    protected String applyGroovism(String line) {
        return line.contains("'") ? line : line.replaceAll("\"", "'");
    }
}
