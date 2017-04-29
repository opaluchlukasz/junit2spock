package com.github.opaluchlukasz.junit2spock.core.feature.junit

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.Expression
import org.eclipse.jdt.core.dom.MethodInvocation
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertTrueFeature.ASSERT_TRUE
import static org.eclipse.jdt.core.dom.AST.*

class AssertTrueFeatureTest extends Specification {

    private static final AST ast = newAST(JLS8)
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })

    @Subject private AssertTrueFeature assertTrueFeature = new AssertTrueFeature(nodeFactory)

    def 'should return false for non assertTrue method invocation'() {
        expect:
        !assertTrueFeature.applicable(node).isPresent()

        where:
        node << [new Object(), nodeFactory.methodInvocation('someMethod', [])]
    }

    def 'should return true for assertTrue method invocation'() {
        given:
        MethodInvocation methodInvocation = nodeFactory.methodInvocation(ASSERT_TRUE,
                [nodeFactory.booleanLiteral(true)])

        expect:
        assertTrueFeature.applicable(methodInvocation).isPresent()
    }

    def 'should return Spock\' expression for proper assertTrue method invocation'() {
        when:
        Expression expression = assertTrueFeature.apply(methodInvocation)

        then:
        expression.toString() == 'true'

        where:
        methodInvocation << [nodeFactory.methodInvocation(ASSERT_TRUE, [nodeFactory.booleanLiteral(true)]),
                             nodeFactory.methodInvocation(ASSERT_TRUE, [nodeFactory.stringLiteral('not true'),
                                                                        nodeFactory.booleanLiteral(true)])]
    }

    def 'should throw an exception for incorrect assertTrue method invocation'() {
        MethodInvocation methodInvocation = nodeFactory.methodInvocation(ASSERT_TRUE,
                [nodeFactory.numberLiteral('0'),
                 nodeFactory.numberLiteral('0'),
                 nodeFactory.numberLiteral('0')])


        when:
        assertTrueFeature.apply(methodInvocation)

        then:
        UnsupportedOperationException ex = thrown()
        ex.message == 'Supported only 1-, 2-arity assertTrue invocation'
    }
}
