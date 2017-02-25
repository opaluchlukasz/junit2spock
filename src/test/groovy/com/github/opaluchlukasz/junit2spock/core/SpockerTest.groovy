package com.github.opaluchlukasz.junit2spock.core

import spock.lang.Specification
import spock.lang.Unroll

import static java.io.File.separator
import static java.nio.charset.StandardCharsets.UTF_8

class SpockerTest extends Specification {

    @Unroll
    def 'should return groovy test class for junit test class (#input)'() {
        given:
        String source = readFromResources("${input}.java")
        String expected = normalize(readFromResources("${input}.groovy"))
        Spocker spocker = new Spocker(source)

        expect:
        normalize(spocker.asGroovyClass()) == expected

        where:
        input << ['MyTest', 'IfStatementWrapperTest']
    }

    def 'should return interface for java interface'() {
        given:
        String source = readFromResources('SomeInterface.java')
        String expected = normalize(readFromResources('SomeInterface.groovy'))
        Spocker spocker = new Spocker(source)

        expect:
        normalize(spocker.asGroovyClass()) == expected
    }

    def 'should return output file path'() {
        given:
        String source = readFromResources('MyTest.java')
        Spocker spocker = new Spocker(source)

        expect:
        spocker.outputFilePath() == "foo${separator}bar${separator}MyTest.groovy"
    }

    private String readFromResources(String filename) {
        new Scanner(getClass().getClassLoader().getResourceAsStream(filename), UTF_8.toString()).useDelimiter("\\A").next()
    }

    private static String normalize(String s) {
        s.replaceAll('\r', '')
    }
}
