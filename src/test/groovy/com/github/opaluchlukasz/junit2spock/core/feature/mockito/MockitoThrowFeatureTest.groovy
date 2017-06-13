package com.github.opaluchlukasz.junit2spock.core.feature.mockito

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.AstProvider
import com.github.opaluchlukasz.junit2spock.core.node.GroovyClosureBuilder
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.Expression
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.MethodInvocation
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.opaluchlukasz.junit2spock.core.builder.ClassInstanceCreationBuilder.aClassInstanceCreation
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.WhenThenThrowFeature.THEN_THROW
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.WhenThenThrowFeature.WHEN
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR
import static java.util.Arrays.asList
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST

class MockitoThrowFeatureTest extends Specification {

    private static final String STUBBED_METHOD_NAME = 'someMethod'
    private static final AST ast = newAST(JLS8)
    @Shared private AstProvider astProvider = {
        get: ast
    }
    @Shared private ASTNodeFactory nf = new ASTNodeFactory(astProvider)
    @Shared private GroovyClosureBuilder groovyClosureBuilder = new GroovyClosureBuilder(astProvider, nf)
    @Shared private MatcherHandler matcherHandler = new MatcherHandler(nf, groovyClosureBuilder)

    @Subject private MockitoThrowFeature mockitoThrowFeature = new MockitoThrowFeature(nf, matcherHandler,
            groovyClosureBuilder, WHEN, THEN_THROW)

    def 'should return false for non thenThrow method invocation'() {
        expect:
        !mockitoThrowFeature.applicable(node).isPresent()

        where:
        node << [new Object(),
                 nf.methodInvocation(STUBBED_METHOD_NAME, []),
                 nf.methodInvocation(THEN_THROW, []),
                 nf.methodInvocation(THEN_THROW, [], nf.methodInvocation(WHEN, [])),
                 nf.methodInvocation(THEN_THROW, [], nf.methodInvocation(WHEN, []))]
    }

    def 'should return true for proper thenThrow method invocation'() {
        given:
        MethodInvocation methodInvocation = nf
                .methodInvocation(THEN_THROW, [],
                        nf.methodInvocation(WHEN, [nf.methodInvocation(STUBBED_METHOD_NAME, [])]))

        expect:
        mockitoThrowFeature.applicable(methodInvocation).isPresent()
    }

    def 'should return Spock\' expression for proper thenThrow method invocation'() {
        given:
        MethodInvocation methodInvocation = thenThrowMethodInvocation()

        InfixExpression expression = mockitoThrowFeature.apply(methodInvocation)

        expect:
        expression.toString() == "$STUBBED_METHOD_NAME() >> {$SEPARATOR\t\t\t" +
                "throw new RuntimeException('some message')\n" +
                "\t\t}" as String
    }

    def 'should apply matchers to stubbed method invocation'() {
        given:
        MethodInvocation methodInvocation = thenThrowMethodInvocation(nf.methodInvocation('any', []))

        when:
        InfixExpression expression = mockitoThrowFeature.apply(methodInvocation)

        then:
        expression.toString() == "$STUBBED_METHOD_NAME(_) >> {$SEPARATOR\t\t\t" +
                "throw new RuntimeException('some message')\n" +
                "\t\t}" as String
    }

    def 'should throw an exception for incorrect thenThrow method invocation'() {
        given:
        MethodInvocation methodInvocation = nf.methodInvocation(THEN_THROW,
                [nf.numberLiteral('0'), nf.numberLiteral('0')])

        when:
        mockitoThrowFeature.apply(methodInvocation, methodInvocation)

        then:
        UnsupportedOperationException ex = thrown()
        ex.message == 'Supported only 1-arity thenThrow invocation'
    }

    private MethodInvocation thenThrowMethodInvocation(Expression... stubbedMethodArguments) {
        def exceptionType = nf.simpleType('RuntimeException')
        def exceptionMessage = nf.stringLiteral('some message')
        nf.methodInvocation(THEN_THROW, [aClassInstanceCreation(ast).withType(exceptionType).withArgument(exceptionMessage).build()],
                nf.methodInvocation(WHEN, [nf.methodInvocation(STUBBED_METHOD_NAME, asList(stubbedMethodArguments))]))
    }
}
