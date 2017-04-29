package com.github.opaluchlukasz.junit2spock.core.feature.mockito

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ExpressionStatement
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.MethodInvocation
import org.eclipse.jdt.core.dom.SimpleName
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static ch.qos.logback.classic.Level.WARN
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.MockitoVerifyFeature.VERIFY
import static org.eclipse.jdt.core.dom.AST.*
import static org.slf4j.LoggerFactory.getLogger

class MockitoVerifyFeatureTest extends Specification {

    private static final AST ast = newAST(JLS8)
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })

    @Subject private MockitoVerifyFeature mockitoVerifyFeature = new MockitoVerifyFeature(nodeFactory)
    private Appender<ILoggingEvent> appender = Mock(Appender)
    private Logger logger = (Logger) getLogger(MockitoVerifyFeature)

    def setup() {
        logger.addAppender(appender)
    }

    def cleanup() {
        logger.detachAppender(appender)
    }

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

    def 'should return Spock\'s mock interaction verification'() {
        when:
        InfixExpression expression = mockitoVerifyFeature.apply(nodeFactory.expressionStatement(methodInvocation))

        then:
        expression.toString() == expected

        where:
        methodInvocation                                                                                                | expected
        nodeFactory.methodInvocation('someMethod', [], verifyInvocation())                                              | '1 * mockedObject.someMethod()'
        nodeFactory.methodInvocation('someMethod', [nodeFactory.numberLiteral('1'), anObject('a')], verifyInvocation()) | '1 * mockedObject.someMethod(1,a)'
    }

    def 'should return Spock\'s mock interaction verification when using VerificationMode'() {
        given:
        def verificationMethodInvocation = nodeFactory.expressionStatement(nodeFactory.methodInvocation('someMethod', [], verifyInvocation(verificationMode)))

        when:
        InfixExpression expression = mockitoVerifyFeature.apply(verificationMethodInvocation)

        then:
        expression.toString() == expected

        where:
        verificationMode                                                          | expected
        nodeFactory.methodInvocation('never', [])                                 | '0 * mockedObject.someMethod()'
        nodeFactory.methodInvocation('atLeastOnce', [])                           | '(1 .. _) * mockedObject.someMethod()'
        nodeFactory.methodInvocation('times', [nodeFactory.numberLiteral('3')])   | '3 * mockedObject.someMethod()'
        nodeFactory.methodInvocation('atMost', [nodeFactory.numberLiteral('3')])  | '(_ .. 3) * mockedObject.someMethod()'
        nodeFactory.methodInvocation('atLeast', [nodeFactory.numberLiteral('3')]) | '(3 .. _) * mockedObject.someMethod()'
    }

    def 'should log warning for unsupported VerificationMode and fallback to single invocation verification'() {
        given:
        def verificationMode = 'neverEver'
        def methodInvocation = nodeFactory.methodInvocation('method', [], verifyInvocation(nodeFactory.methodInvocation(verificationMode, [])))

        when:
        InfixExpression expression = mockitoVerifyFeature.apply(nodeFactory.expressionStatement(methodInvocation))

        then:
        1 * appender.doAppend({ LoggingEvent event ->
            event.level == WARN && event.message == "Unsupported VerificationMode: $verificationMode"
        } as ILoggingEvent)
        expression.toString() == '1 * mockedObject.method()'
    }

    def 'should replace anyObject matcher with Spock\'s wildcard'() {
        given:
        def methodInvocation = nodeFactory.methodInvocation('method', [nodeFactory.numberLiteral("1"), nodeFactory.methodInvocation('anyObject', [])], verifyInvocation())

        when:
        InfixExpression expression = mockitoVerifyFeature.apply(nodeFactory.expressionStatement(methodInvocation))

        then:
        expression.toString() == '1 * mockedObject.method(1,_)'
    }

    @Unroll
    def 'should replace #matcherMethod matcher with Spock\'s wildcard with cast'() {
        given:
        def methodInvocation = nodeFactory.methodInvocation('method', [nodeFactory.methodInvocation(matcherMethod, [])], verifyInvocation())

        when:
        InfixExpression expression = mockitoVerifyFeature.apply(nodeFactory.expressionStatement(methodInvocation))

        then:
        expression.toString() == "1 * mockedObject.method(_ as ${clazz.simpleName}.class)"

        where:
        clazz      | matcherMethod
        Byte       | 'anyByte'
        Character  | 'anyChar'
        Integer    | 'anyInt'
        Long       | 'anyLong'
        Float      | 'anyFloat'
        Double     | 'anyDouble'
        Short      | 'anyShort'
        String     | 'anyString'
        List       | 'anyList'
        Set        | 'anySet'
        Map        | 'anyMap'
        Collection | 'anyCollection'
        Iterable   | 'anyIterable'
    }

    private MethodInvocation verifyInvocation() {
        nodeFactory.methodInvocation(VERIFY, [anObject('mockedObject')])
    }

    private MethodInvocation verifyInvocation(MethodInvocation verificationMode) {
        nodeFactory.methodInvocation(VERIFY, [anObject('mockedObject'), verificationMode])
    }

    private SimpleName anObject(String name) {
        nodeFactory.simpleName(name)
    }
}