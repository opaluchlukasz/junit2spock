package com.github.opaluchlukasz.junit2spock.core.groovism

import spock.lang.Specification
import spock.lang.Subject

class NoSemicolonTest extends Specification {

    @Subject private NoSemicolon noSemicolon = new NoSemicolon()

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
