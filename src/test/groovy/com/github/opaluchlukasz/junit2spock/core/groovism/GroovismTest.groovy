package com.github.opaluchlukasz.junit2spock.core.groovism

import spock.lang.Specification

import static java.util.Optional.empty

class GroovismTest extends Specification {

    def 'should apply chain of groovisms'() {
        given:
        def groovism = new Reverse(Optional.of(new ToUpperCase(empty())))

        expect:
        groovism.apply('someText') == 'TXETEMOS'
    }

    private static class Reverse extends Groovism {
        Reverse(Optional<Groovism> next) {
            super(next)
        }

        @Override protected String applyGroovism(String line) {
            line.reverse()
        }
    }

    private static class ToUpperCase extends Groovism {
        ToUpperCase(Optional<Groovism> next) {
            super(next)
        }

        @Override protected String applyGroovism(String line) {
            line.toUpperCase()
        }
    }
}
