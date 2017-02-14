package com.github.opaluchlukasz.junit2spock.core.feature

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.MethodInvocation
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.opaluchlukasz.junit2spock.core.feature.AssertEqualsFeature.ASSERT_EQUALS

class AssertEqualsFeatureTest extends Specification {

    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory()

    @Subject AssertEqualsFeature assertEqualsFeature = new AssertEqualsFeature(nodeFactory)

    def 'should return false for non assertEquals method invocation'() {
        expect:
        !assertEqualsFeature.applicable(node)

        where:
        node << [new Object(), nodeFactory.methodInvocation('someMethod', [])]
    }

    def 'should return true for assertEquals method invocation'() {
        given:
        MethodInvocation methodInvocation = nodeFactory.methodInvocation(ASSERT_EQUALS,
                [nodeFactory.numberLiteral("0"), nodeFactory.numberLiteral("0")])

        expect:
        assertEqualsFeature.applicable(methodInvocation)
    }

    def 'should return Spock\' expression for proper assertEquals method invocation'() {
        when:
        InfixExpression expression = assertEqualsFeature.apply(methodInvocation)

        then:
        expression.toString() == '0 == 0'

        where:
        methodInvocation << [nodeFactory.methodInvocation(ASSERT_EQUALS, [nodeFactory.numberLiteral("0"),
                                                                          nodeFactory.numberLiteral("0")]),
                             nodeFactory.methodInvocation(ASSERT_EQUALS, [nodeFactory.stringLiteral('equal to null'),
                                                                          nodeFactory.numberLiteral('0'),
                                                                          nodeFactory.numberLiteral('0')])]
    }

    def 'should throw an exception for incorrect assertEquals method invocation'() {
        MethodInvocation methodInvocation = nodeFactory.methodInvocation(ASSERT_EQUALS,
                [nodeFactory.numberLiteral('0'),
                 nodeFactory.numberLiteral('0'),
                 nodeFactory.numberLiteral('0'),
                 nodeFactory.numberLiteral('0')])


        when:
        assertEqualsFeature.apply(methodInvocation)

        then:
        UnsupportedOperationException ex = thrown()
        ex.message == 'Supported only 2-, 3-arity assertEquals/assertArrayEquals invocation'
    }
}
