package com.github.opaluchlukasz.junit2spock.core.feature.mockito

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.AstProvider
import com.github.opaluchlukasz.junit2spock.core.node.GroovyClosureBuilder
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodInvocation
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.WhenThenReturnFeature.THEN_RETURN
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.WhenThenReturnFeature.WHEN
import static org.eclipse.jdt.core.dom.AST.newAST

class MockitoReturnFeatureTest extends Specification {

    private static final String STUBBED_METHOD_NAME = 'someMethod'
    private static final AST ast = newAST(AST.JLS8)
    @Shared private AstProvider astProvider = {
        get: ast
    }
    @Shared private ASTNodeFactory nf = new ASTNodeFactory(astProvider)
    @Shared private GroovyClosureBuilder groovyClosureBuilder = new GroovyClosureBuilder(astProvider, nf)
    @Shared private MatcherHandler matcherHandler = new MatcherHandler(nf, groovyClosureBuilder)

    @Subject private MockitoReturnFeature returnFeature = new MockitoReturnFeature(nf, matcherHandler, WHEN, THEN_RETURN)

    def 'should return false for non thenReturn method invocation'() {
        expect:
        !returnFeature.applicable(node).isPresent()

        where:
        node << [new Object(),
                 nf.methodInvocation(STUBBED_METHOD_NAME, []),
                 nf.methodInvocation(THEN_RETURN, []),
                 nf.methodInvocation(THEN_RETURN, [], nf.methodInvocation(WHEN, [])),
                 nf.methodInvocation(THEN_RETURN, [], nf.methodInvocation(WHEN, []))]
    }

    def 'should return true for proper thenReturn method invocation'() {
        given:
        MethodInvocation methodInvocation = nf
                .methodInvocation(THEN_RETURN, [],
                        nf.methodInvocation(WHEN, [nf.methodInvocation('someMethod', [])]))

        expect:
        returnFeature.applicable(methodInvocation).isPresent()
    }

    def 'should return Spock\' expression for proper thenReturn method invocation'() {
        given:
        MethodInvocation methodInvocation = nf
                .methodInvocation(THEN_RETURN, [nf.booleanLiteral(true)],
                nf.methodInvocation(WHEN, [nf.methodInvocation(STUBBED_METHOD_NAME, [])]))
        Object expression = returnFeature.apply(methodInvocation)

        expect:
        expression.toString() == "$STUBBED_METHOD_NAME() >> true" as String
    }

    def 'should return Spock\' expression for sequenced of returned data'() {
        given:
        MethodInvocation methodInvocation = nf
                .methodInvocation(THEN_RETURN, [nf.booleanLiteral(true), nf.booleanLiteral(false)],
                nf.methodInvocation(WHEN, [nf.methodInvocation(STUBBED_METHOD_NAME, [])]))
        Object expression = returnFeature.apply(methodInvocation)

        expect:
        expression.toString() == "$STUBBED_METHOD_NAME() >>> [true, false]" as String
    }

    def 'should apply matchers to stubbed method invocation'() {
        given:
        MethodInvocation methodInvocation = nf
                .methodInvocation(THEN_RETURN, [nf.booleanLiteral(true)],
                nf.methodInvocation(WHEN, [nf.methodInvocation(STUBBED_METHOD_NAME, [nf.methodInvocation('anyString', [])])]))

        when:
        Object expression = returnFeature.apply(methodInvocation)

        then:
        expression.toString() == "$STUBBED_METHOD_NAME(_ as String.class) >> true" as String
    }
}
