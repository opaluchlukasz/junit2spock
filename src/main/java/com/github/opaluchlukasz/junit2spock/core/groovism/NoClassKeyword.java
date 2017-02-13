package com.github.opaluchlukasz.junit2spock.core.groovism;

import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Optional.empty;

public class NoClassKeyword extends Groovism {

    NoClassKeyword() {
        super(empty());
    }

    NoClassKeyword(Groovism next) {
        super(Optional.of(next));
    }

    @Override
    protected String applyGroovism(String line) {
        return line.replaceAll(Pattern.quote(".class"), "");
    }
}
