package com.github.opaluchlukasz.junit2spock.core.feature

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.ExpressionStatement
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.MethodInvocation
import org.eclipse.jdt.core.dom.SimpleName
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static ch.qos.logback.classic.Level.WARN
import static com.github.opaluchlukasz.junit2spock.core.feature.MockitoVerifyFeature.VERIFY
import static org.slf4j.LoggerFactory.getLogger

class MockitoVerifyFeatureTest extends Specification {

    @Subject private MockitoVerifyFeature mockitoVerifyFeature = new MockitoVerifyFeature(nodeFactory)
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory()
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

    def 'should return Spock\' mock interaction verification when using VerificationMode'() {
        given:
        def verificationMethodInvocation = nodeFactory.expressionStatement(nodeFactory.methodInvocation('someMethod', [], verifyInvocation(verificationMode)))

        when:
        InfixExpression expression = mockitoVerifyFeature.apply(verificationMethodInvocation)

        then:
        expression.toString() == expected

        where:
        verificationMode                                                        | expected
        nodeFactory.methodInvocation('never', [])                               | '0 * mockedObject.someMethod()'
        nodeFactory.methodInvocation('times', [nodeFactory.numberLiteral('3')]) | '3 * mockedObject.someMethod()'
    }

    def
    'should log warning for unsupported VerificationMode and fallback to any VerificationMode'() {
        given:
        def verificationMode = 'neverEver'
        def methodInvocation = nodeFactory.methodInvocation('method', [], verifyInvocation(nodeFactory.methodInvocation(verificationMode, [])))

        when:
        InfixExpression expression = mockitoVerifyFeature.apply(nodeFactory.expressionStatement(methodInvocation))

        then:
        1 * appender.doAppend({ LoggingEvent event ->
            event.level == WARN && event.message == "Unsupported VerificationMode: $verificationMode"
        } as ILoggingEvent)
        expression.toString() == '_ * mockedObject.method()'
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
