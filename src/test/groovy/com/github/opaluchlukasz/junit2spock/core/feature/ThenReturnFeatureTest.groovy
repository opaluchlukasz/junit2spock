package com.github.opaluchlukasz.junit2spock.core.feature

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.MethodInvocation
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.opaluchlukasz.junit2spock.core.feature.ThenReturnFeature.THEN_RETURN
import static com.github.opaluchlukasz.junit2spock.core.feature.ThenReturnFeature.WHEN

class ThenReturnFeatureTest extends Specification {

    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory()

    @Subject private ThenReturnFeature thenReturnFeature = new ThenReturnFeature(nodeFactory)

    def 'should return false for non thenReturn method invocation'() {
        expect:
        !thenReturnFeature.applicable(node).isPresent()

        where:
        node << [new Object(),
                 nodeFactory.methodInvocation('someMethod', []),
                 nodeFactory.methodInvocation(THEN_RETURN, []),
                 nodeFactory.methodInvocation(THEN_RETURN, [], nodeFactory.methodInvocation(WHEN, [])),
                 nodeFactory.methodInvocation(THEN_RETURN, [], nodeFactory.methodInvocation(WHEN, []))]
    }

    def 'should return true for proper thenReturn method invocation'() {
        given:
        MethodInvocation methodInvocation = nodeFactory
                .methodInvocation(THEN_RETURN, [],
                        nodeFactory.methodInvocation(WHEN, [nodeFactory.methodInvocation('someMethod', [])]))

        expect:
        thenReturnFeature.applicable(methodInvocation).isPresent()
    }

    def 'should return Spock\' expression for proper thenReturn method invocation'() {
        given:
        def stubbedMethod = 'someMethod'
        MethodInvocation methodInvocation = nodeFactory
                .methodInvocation(THEN_RETURN, [nodeFactory.booleanLiteral(true)],
                nodeFactory.methodInvocation(WHEN, [nodeFactory.methodInvocation(stubbedMethod, [])]))
        InfixExpression expression = thenReturnFeature.apply(methodInvocation)

        expect:
        expression.toString() == "$stubbedMethod() >> true" as String
    }

    def 'should throw an exception for incorrect thenReturn method invocation'() {
        given:
        MethodInvocation methodInvocation = nodeFactory.methodInvocation(THEN_RETURN,
                [nodeFactory.numberLiteral('0'), nodeFactory.numberLiteral('0')])


        when:
        thenReturnFeature.apply(methodInvocation, methodInvocation)

        then:
        UnsupportedOperationException ex = thrown()
        ex.message == 'Supported only 1-arity thenReturn invocation'
    }
}
