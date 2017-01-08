package com.github.opaluchlukasz.junit2spock.core

import spock.lang.Specification

import static java.nio.charset.StandardCharsets.UTF_8

class SpockerTest extends Specification {

    def 'should return groovy class for java source'() {
        given:
        Spocker spocker = new Spocker()
        String source = readFromResources('MyTest.java')
        String expected = normalize(readFromResources('MyTest.groovy'))

        expect:
        normalize(spocker.parse(source)) == expected
    }

    private String readFromResources(String filename) {
        new Scanner(getClass().getClassLoader().getResourceAsStream(filename), UTF_8.toString()).useDelimiter("\\A").next()
    }

    private static String normalize(String s) {
        s.replaceAll('\r', '')
    }
}
