package com.github.opaluchlukasz.junit2spock.core.node

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import spock.lang.Shared
import spock.lang.Specification

class SpockMockReturnSequencesTest extends Specification {

    @Shared private static ASTNodeFactory nf = new ASTNodeFactory()

    def 'should have proper toString method'() {
        given:
        SpockMockReturnSequences returnSequences = new SpockMockReturnSequences(nf.methodInvocation('a', []),
                sequence)

        expect:
        returnSequences.toString() == expected

        where:
        sequence                                                              | expected
        [nf.numberLiteral('1'), nf.numberLiteral('2'), nf.numberLiteral('3')] | 'a() >>> [1, 2, 3]'
        [nf.numberLiteral('1'), nf.methodInvocation('some', [])]              | 'a() >>> [1, some()]'
    }
}
