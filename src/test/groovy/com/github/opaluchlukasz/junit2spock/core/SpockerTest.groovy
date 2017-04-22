package com.github.opaluchlukasz.junit2spock.core

import spock.lang.Specification
import spock.lang.Unroll

import static java.io.File.separator
import static java.nio.charset.StandardCharsets.UTF_8

class SpockerTest extends Specification {

    @Unroll
    def 'should return groovy class for java class (#input)'() {
        given:
        String source = readFromResources("${input}.java")
        String expected = normalize(readFromResources("${input}.groovy"))
        Spocker spocker = new Spocker(source)

        expect:
        normalize(spocker.asGroovyClass()) == expected

        where:
        input << ['MockitoTest', 'Junit4Test', 'IfStatementWrapperTest', 'InnerType']
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
        String source = readFromResources('Junit4Test.java')
        Spocker spocker = new Spocker(source)

        expect:
        spocker.outputFilePath() == "foo${separator}bar${separator}Junit4Test.groovy"
    }

    def 'should transform given/when/then comments into Spock\'s blocks'() {
        given:
        String source = readFromResources('CommentsAsMarkerForBlocks.java')
        String expected = normalize(readFromResources('CommentsAsMarkerForBlocks.groovy'))
        Spocker spocker = new Spocker(source)

        expect:
        normalize(spocker.asGroovyClass()) == expected
    }

    private String readFromResources(String filename) {
        new Scanner(getClass().getClassLoader().getResourceAsStream(filename), UTF_8.toString()).useDelimiter("\\A").next()
    }

    private static String normalize(String s) {
        s.replaceAll('\r', '')
    }
}
