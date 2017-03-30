package com.github.opaluchlukasz.junit2spock.core.feature

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.ExpressionStatement
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.MethodInvocation
import org.eclipse.jdt.core.dom.SimpleName
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.opaluchlukasz.junit2spock.core.feature.MockitoVerifyFeature.VERIFY

class MockitoVerifyFeatureTest extends Specification {

    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory()
    @Subject private MockitoVerifyFeature mockitoVerifyFeature = new MockitoVerifyFeature(nodeFactory)

    def 'should return false for non verify method invocation'() {
        expect:
        !mockitoVerifyFeature.applicable(node).isPresent()

        where:
        node << [new Object(),
                 nodeFactory.expressionStatement(nodeFactory.methodInvocation('someMethod', [])),
                 nodeFactory.expressionStatement(nodeFactory.methodInvocation(VERIFY, [])),
                 nodeFactory.expressionStatement(nodeFactory.methodInvocation('someMethod', [], nodeFactory.methodInvocation(VERIFY, []))),
                 nodeFactory.expressionStatement(nodeFactory.methodInvocation('someMethod', [], nodeFactory.methodInvocation(VERIFY, [])))]
    }

    def 'should return true for proper verify method invocation'() {
        given:
        ExpressionStatement expressionStatement = nodeFactory.expressionStatement(nodeFactory
                .methodInvocation('someMethod', [], nodeFactory.methodInvocation(VERIFY, [nodeFactory.simpleName('mockedObject')])))

        expect:
        mockitoVerifyFeature.applicable(expressionStatement).isPresent()
    }

    def 'should return Spock\' mock interaction verification'() {
        when:
        InfixExpression expression = mockitoVerifyFeature.apply(nodeFactory.expressionStatement(methodInvocation))

        then:
        expression.toString() == expected

        where:
        methodInvocation                                                                                                | expected
        nodeFactory.methodInvocation('someMethod', [], verifyInvocation())                                              | '_ * mockedObject.someMethod()'
        nodeFactory.methodInvocation('someMethod', [nodeFactory.numberLiteral('1'), anObject('a')], verifyInvocation()) | '_ * mockedObject.someMethod(1,a)'
    }

    private MethodInvocation verifyInvocation() {
        nodeFactory.methodInvocation(VERIFY, [anObject('mockedObject')])
    }

    private SimpleName anObject(String name) {
        nodeFactory.simpleName(name)
    }
}
