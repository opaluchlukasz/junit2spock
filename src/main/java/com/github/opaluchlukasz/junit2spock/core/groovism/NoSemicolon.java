package com.github.opaluchlukasz.junit2spock.core.groovism;

import java.util.Optional;

public class NoSemicolon extends Groovism {

    NoSemicolon(Optional<Groovism> next) {
        super(next);
    }

    @Override
    protected String applyGroovism(String line) {
        return line.replaceAll(";+$", "");
    }
}
