package com.github.opaluchlukasz.junit2spock.core.groovism;

import java.util.Optional;

import static java.util.Optional.empty;

public class NoSemicolon extends Groovism {

    NoSemicolon() {
        super(empty());
    }

    NoSemicolon(Groovism next) {
        super(Optional.of(next));
    }

    @Override
    protected String applyGroovism(String line) {
        return line.replaceAll(";+$", "");
    }
}
