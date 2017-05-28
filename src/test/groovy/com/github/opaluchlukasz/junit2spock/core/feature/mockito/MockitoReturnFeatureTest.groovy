package com.github.opaluchlukasz.junit2spock.core.feature.mockito

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.AstProvider
import com.github.opaluchlukasz.junit2spock.core.node.GroovyClosureFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodInvocation
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.WhenThenReturnFeature.THEN_RETURN
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.WhenThenReturnFeature.WHEN

class MockitoReturnFeatureTest extends Specification {

    private static final AST ast = AST.newAST(AST.JLS8)
    @Shared private AstProvider astProvider = {
        get: ast
    }
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory(astProvider)
    @Shared private GroovyClosureFactory groovyClosureFactory = new GroovyClosureFactory(astProvider)
    @Shared private MatcherHandler matcherHandler = new MatcherHandler(nodeFactory, groovyClosureFactory)

    @Subject private MockitoReturnFeature returnFeature = new MockitoReturnFeature(nodeFactory, matcherHandler, WHEN, THEN_RETURN)

    def 'should return false for non thenReturn method invocation'() {
        expect:
        !returnFeature.applicable(node).isPresent()

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
        returnFeature.applicable(methodInvocation).isPresent()
    }

    def 'should return Spock\' expression for proper thenReturn method invocation'() {
        given:
        def stubbedMethod = 'someMethod'
        MethodInvocation methodInvocation = nodeFactory
                .methodInvocation(THEN_RETURN, [nodeFactory.booleanLiteral(true)],
                nodeFactory.methodInvocation(WHEN, [nodeFactory.methodInvocation(stubbedMethod, [])]))
        Object expression = returnFeature.apply(methodInvocation)

        expect:
        expression.toString() == "$stubbedMethod() >> true" as String
    }

    def 'should return Spock\' expression for sequenced of returned data'() {
        given:
        def stubbedMethod = 'someMethod'
        MethodInvocation methodInvocation = nodeFactory
                .methodInvocation(THEN_RETURN, [nodeFactory.booleanLiteral(true), nodeFactory.booleanLiteral(false)],
                nodeFactory.methodInvocation(WHEN, [nodeFactory.methodInvocation(stubbedMethod, [])]))
        Object expression = returnFeature.apply(methodInvocation)

        expect:
        expression.toString() == "$stubbedMethod() >>> [true, false]" as String
    }
}
