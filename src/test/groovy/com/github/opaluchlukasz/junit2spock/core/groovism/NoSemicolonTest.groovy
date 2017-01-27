package com.github.opaluchlukasz.junit2spock.core.groovism

import spock.lang.Specification
import spock.lang.Subject

import static java.util.Optional.empty

class NoSemicolonTest extends Specification {

    @Subject private NoSemicolon noSemicolon = new NoSemicolon(empty())

    def 'should replace all semicolons at the end of the line'() {
        expect:
        noSemicolon.apply(line) == expectedLine

        where:
        line       | expectedLine
        ''         | ''
        'some'     | 'some'
        'some;'    | 'some'
        'some;;'   | 'some'
        's;o;m;e;' | 's;o;m;e'
    }
}
