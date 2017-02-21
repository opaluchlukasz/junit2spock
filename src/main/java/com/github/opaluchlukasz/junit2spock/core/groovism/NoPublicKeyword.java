package com.github.opaluchlukasz.junit2spock.core.groovism;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.regex.Pattern.quote;

public class NoPublicKeyword extends Groovism {

    NoPublicKeyword() {
        super(empty());
    }

    NoPublicKeyword(Groovism next) {
        super(Optional.of(next));
    }

    @Override
    protected String applyGroovism(String line) {
        String applied = line.replaceAll(quote(" public "), " ");
        if (applied.startsWith("public ")) {
            return applied.replaceFirst(quote("public "), "");
        }
        return applied;
    }
}
