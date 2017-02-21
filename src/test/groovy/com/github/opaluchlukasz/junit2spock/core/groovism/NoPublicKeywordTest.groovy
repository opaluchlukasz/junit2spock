package com.github.opaluchlukasz.junit2spock.core.groovism

import spock.lang.Specification
import spock.lang.Subject

class NoPublicKeywordTest extends Specification {

    @Subject private NoPublicKeyword noPublicKeyword = new NoPublicKeyword()

    def 'should remove all public keywords'() {
        expect:
        noPublicKeyword.apply(line) == expectedLine

        where:
        line             | expectedLine
        ''               | ''
        'some'           | 'some'
        'somepublic '    | 'somepublic '
        ' public String' | ' String'
        'public String'  | 'String'
    }
}
