package com.github.opaluchlukasz.junit2spock.core.feature.mockito

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.AstProvider
import com.github.opaluchlukasz.junit2spock.core.node.GroovyClosureFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.MethodInvocation
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.WhenThenThrowFeature.THEN_THROW
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.WhenThenThrowFeature.WHEN
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST

class WhenThenThrowFeatureTest extends Specification {

    private static final AST ast = newAST(JLS8)
    private static final AstProvider AST_PROVIDER = {
        get: ast
    }
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory(AST_PROVIDER)

    @Subject private WhenThenThrowFeature thenThrowFeature = new WhenThenThrowFeature(nodeFactory,
            new GroovyClosureFactory(AST_PROVIDER))

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
        def exceptionType = nodeFactory.simpleType(nodeFactory.simpleName('RuntimeException'))
        def exceptionMessage = nodeFactory.stringLiteral('some message')
        MethodInvocation methodInvocation = nodeFactory
                .methodInvocation(THEN_THROW, [nodeFactory.classInstanceCreation(exceptionType, exceptionMessage)],
                nodeFactory.methodInvocation(WHEN, [nodeFactory.methodInvocation(stubbedMethod, [])]))
        InfixExpression expression = thenThrowFeature.apply(methodInvocation)

        expect:
        expression.toString() == "$stubbedMethod() >> {\n\t\t\t" +
                "throw new RuntimeException('some message')\n" +
                "\t\t}" as String
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
