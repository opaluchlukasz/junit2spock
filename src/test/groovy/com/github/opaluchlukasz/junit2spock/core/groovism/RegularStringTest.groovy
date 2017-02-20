package com.github.opaluchlukasz.junit2spock.core.groovism

import spock.lang.Specification
import spock.lang.Subject

class RegularStringTest extends Specification {

    @Subject private RegularString regularString = new RegularString()

    def 'should replace all GString with regular Groovy String'() {
        expect:
        regularString.apply(line) == expectedLine

        where:
        line                                       | expectedLine
        '""'                                       | "''"
        'new String("some") == new String("some")' | "new String('some') == new String('some')"
    }

    def 'should mot replace GString with regular Groovy String when line contains apostrophe'() {
        expect:
        regularString.apply(line) == expectedLine

        where:
        line                   | expectedLine
        "\"'\""                | "\"'\""
        "\"someone's string\"" | "\"someone's string\""
    }
}
