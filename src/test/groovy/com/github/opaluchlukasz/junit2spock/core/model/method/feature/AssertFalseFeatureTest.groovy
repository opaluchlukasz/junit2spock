package com.github.opaluchlukasz.junit2spock.core.model.method.feature

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.Expression
import org.eclipse.jdt.core.dom.MethodInvocation
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.opaluchlukasz.junit2spock.core.model.method.feature.AssertFalseFeature.ASSERT_FALSE

class AssertFalseFeatureTest extends Specification {

    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory()

    @Subject private AssertFalseFeature assertFalseFeature = new AssertFalseFeature(nodeFactory)

    def 'should return false for non assertFalse method invocation'() {
        expect:
        !assertFalseFeature.applicable(node)

        where:
        node << [new Object(), nodeFactory.methodInvocation('someMethod', [])]
    }

    def 'should return true for assertFalse method invocation'() {
        given:
        MethodInvocation methodInvocation = nodeFactory.methodInvocation(ASSERT_FALSE,
                [nodeFactory.booleanLiteral(true)])

        expect:
        assertFalseFeature.applicable(methodInvocation)
    }

    def 'should return Spock\' expression for proper assertFalse method invocation'() {
        when:
        Expression expression = assertFalseFeature.apply(methodInvocation)

        then:
        expression.toString() == '!false'

        where:
        methodInvocation << [nodeFactory.methodInvocation(ASSERT_FALSE, [nodeFactory.booleanLiteral(false)]),
                             nodeFactory.methodInvocation(ASSERT_FALSE, [nodeFactory.stringLiteral('so true'),
                                                                        nodeFactory.booleanLiteral(false)])]
    }

    def 'should throw an exception for incorrect assertFalse method invocation'() {
        MethodInvocation methodInvocation = nodeFactory.methodInvocation(ASSERT_FALSE,
                [nodeFactory.numberLiteral('0'),
                 nodeFactory.numberLiteral('0'),
                 nodeFactory.numberLiteral('0')])


        when:
        assertFalseFeature.apply(methodInvocation)

        then:
        UnsupportedOperationException ex = thrown()
        ex.message == 'Supported only 1-, 2-arity assertFalse invocation'
    }
}
