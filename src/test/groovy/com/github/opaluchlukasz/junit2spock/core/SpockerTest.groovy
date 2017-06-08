package com.github.opaluchlukasz.junit2spock.core

import com.github.opaluchlukasz.junit2spock.core.util.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static java.nio.charset.StandardCharsets.UTF_8

@ContextConfiguration(classes = TestConfig.class)
class SpockerTest extends Specification {

    @Autowired @Subject private Spocker spocker

    @Unroll
    def 'should return groovy class for java class (#input)'() {
        given:
        String source = readFromResources("${input}.java")
        String expected = normalize(readFromResources("${input}.groovy"))

        expect:
        normalize(spocker.toGroovyTypeModel(source).asGroovyClass(0)) == expected

        where:
        input << ['MockitoTest', 'Junit4Test', 'WrappersTest', 'InnerType']
    }

    def 'should return interface for java interface'() {
        given:
        String source = readFromResources('SomeInterface.java')
        String expected = normalize(readFromResources('SomeInterface.groovy'))

        expect:
        normalize(spocker.toGroovyTypeModel(source).asGroovyClass(0)) == expected
    }

    def 'should transform given/when/then comments into Spock\'s blocks'() {
        given:
        String source = readFromResources('CommentsAsMarkerForBlocks.java')
        String expected = normalize(readFromResources('CommentsAsMarkerForBlocks.groovy'))

        expect:
        normalize(spocker.toGroovyTypeModel(source).asGroovyClass(0)) == expected
    }

    private String readFromResources(String filename) {
        new Scanner(getClass().getClassLoader().getResourceAsStream(filename), UTF_8.toString()).useDelimiter("\\A").next()
    }

    private static String normalize(String s) {
        s.replaceAll('\r', '')
    }
}
