package com.github.opaluchlukasz.junit2spock.core.groovism

import spock.lang.Specification
import spock.lang.Subject

class NoClassKeywordTest extends Specification {

    @Subject private NoClassKeyword noClassKeyword = new NoClassKeyword()

    def 'should replace all .class in class literals'() {
        expect:
        noClassKeyword.apply(line) == expectedLine

        where:
        line         | expectedLine
        ''           | ''
        'some'       | 'some'
        'someclass'  | 'someclass'
        'some.Class' | 'some.Class'
        'some.class' | 'some'
    }
}
