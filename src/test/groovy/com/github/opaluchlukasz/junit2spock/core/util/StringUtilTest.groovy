package com.github.opaluchlukasz.junit2spock.core.util

import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.indent
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.indentation

class StringUtilTest extends Specification {

    def 'should return proper indentation in tabs'() {
        expect:
        indentation(tabs) == expected

        where:
        tabs | expected
        -1   | ''
        0    | ''
        1    | '\t'
        3    | '\t\t\t'
    }

    def 'should append proper indentation in tabs'() {
        expect:
        indent(new StringBuilder(), tabs).toString() == expected

        where:
        tabs | expected
        -1   | ''
        0    | ''
        1    | '\t'
        3    | '\t\t\t'
    }
}
