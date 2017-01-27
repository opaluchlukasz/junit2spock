package com.github.opaluchlukasz.junit2spock.core.groovism;

import java.util.Optional;
import java.util.function.UnaryOperator;

public abstract class Groovism implements UnaryOperator<String> {

    private final Optional<Groovism> next;

    Groovism(Optional<Groovism> next) {
        this.next = next;
    }

    @Override
    public String apply(String line) {
        String groovierLine = applyGroovism(line);
        return next.map(next -> next.applyGroovism(groovierLine)).orElse(groovierLine);
    }

    protected abstract String applyGroovism(String line);
}