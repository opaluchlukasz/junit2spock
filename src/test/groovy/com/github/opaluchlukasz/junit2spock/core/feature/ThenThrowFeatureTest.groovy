package com.github.opaluchlukasz.junit2spock.core.feature

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.node.SpockMockBehaviour
import org.eclipse.jdt.core.dom.MethodInvocation
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.opaluchlukasz.junit2spock.core.feature.ThenThrowFeature.THEN_THROW
import static com.github.opaluchlukasz.junit2spock.core.feature.ThenThrowFeature.WHEN

class ThenThrowFeatureTest extends Specification {

    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory()

    @Subject private ThenThrowFeature thenThrowFeature = new ThenThrowFeature(nodeFactory)

    def 'should return false for non thenThrow method invocation'() {
        expect:
        !thenThrowFeature.applicable(node).isPresent()

        where:
        node << [new Object(),
                 nodeFactory.methodInvocation('someMethod', []),
                 nodeFactory.methodInvocation(THEN_THROW, []),
                 nodeFactory.methodInvocation(THEN_THROW, [], nodeFactory.methodInvocation(WHEN, [])),
                 nodeFactory.methodInvocation(THEN_THROW, [], nodeFactory.methodInvocation(WHEN, []))]
    }

    def 'should return true for proper thenThrow method invocation'() {
        given:
        MethodInvocation methodInvocation = nodeFactory
                .methodInvocation(THEN_THROW, [],
                        nodeFactory.methodInvocation(WHEN, [nodeFactory.methodInvocation('someMethod', [])]))

        expect:
        thenThrowFeature.applicable(methodInvocation).isPresent()
    }

    def 'should return Spock\' expression for proper thenThrow method invocation'() {
        given:
        def stubbedMethod = 'someMethod'
        def exceptionType = nodeFactory.simpleType('RuntimeException')
        def exceptionMessage = nodeFactory.stringLiteral('some message')
        MethodInvocation methodInvocation = nodeFactory
                .methodInvocation(THEN_THROW, [nodeFactory.classInstanceCreation(exceptionType, exceptionMessage)],
                nodeFactory.methodInvocation(WHEN, [nodeFactory.methodInvocation(stubbedMethod, [])]))
        SpockMockBehaviour expression = thenThrowFeature.apply(methodInvocation)

        expect:
        expression.toString() == "$stubbedMethod() >> {\n" +
                "  throw new RuntimeException(\"some message\");\n" +
                "}\n" as String
    }

    def 'should throw an exception for incorrect thenThrow method invocation'() {
        given:
        MethodInvocation methodInvocation = nodeFactory.methodInvocation(THEN_THROW,
                [nodeFactory.numberLiteral('0'), nodeFactory.numberLiteral('0')])


        when:
        thenThrowFeature.apply(methodInvocation, methodInvocation)

        then:
        UnsupportedOperationException ex = thrown()
        ex.message == 'Supported only 1-arity thenThrow invocation'
    }
}
