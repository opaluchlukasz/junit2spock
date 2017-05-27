package com.github.opaluchlukasz.junit2spock.core.feature.mockito

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.AstProvider
import com.github.opaluchlukasz.junit2spock.core.node.GroovyClosureFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ExpressionStatement
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.MethodInvocation
import org.eclipse.jdt.core.dom.SimpleName
import org.eclipse.jdt.core.dom.TypeLiteral
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static ch.qos.logback.classic.Level.WARN
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.MockitoVerifyFeature.VERIFY
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST
import static org.slf4j.LoggerFactory.getLogger

class MockitoVerifyFeatureTest extends Specification {

    private static final AST ast = newAST(JLS8)
    private static final AstProvider AST_PROVIDER = {
        get: ast
    }
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory(AST_PROVIDER)
    @Shared private GroovyClosureFactory groovyClosureFactory = new GroovyClosureFactory(AST_PROVIDER)

    @Subject private MockitoVerifyFeature mockitoVerifyFeature = new MockitoVerifyFeature(nodeFactory,
            new MatcherHandler(nodeFactory, groovyClosureFactory))
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
                .methodInvocation('someMethod', [], nodeFactory.methodInvocation(VERIFY, [nodeFactory.simpleName('mockedType')])))

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
        nodeFactory.methodInvocation('someMethod', [], verifyInvocation())                                              | '1 * mockedType.someMethod()'
        nodeFactory.methodInvocation('someMethod', [nodeFactory.numberLiteral('1'), anObject('a')], verifyInvocation()) | '1 * mockedType.someMethod(1,a)'
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
        nodeFactory.methodInvocation('never', [])                                 | '0 * mockedType.someMethod()'
        nodeFactory.methodInvocation('atLeastOnce', [])                           | '(1 .. _) * mockedType.someMethod()'
        nodeFactory.methodInvocation('times', [nodeFactory.numberLiteral('3')])   | '3 * mockedType.someMethod()'
        nodeFactory.methodInvocation('atMost', [nodeFactory.numberLiteral('3')])  | '(_ .. 3) * mockedType.someMethod()'
        nodeFactory.methodInvocation('atLeast', [nodeFactory.numberLiteral('3')]) | '(3 .. _) * mockedType.someMethod()'
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
        expression.toString() == '1 * mockedType.method()'
    }

    @Unroll
    def 'should replace #matcherMethod matcher with Spock\'s expression'() {
        given:
        def methodInvocation = nodeFactory.methodInvocation('method', [nodeFactory.methodInvocation(matcherMethod, arguments)], verifyInvocation())

        when:
        Object expression = mockitoVerifyFeature.apply(nodeFactory.expressionStatement(methodInvocation))

        then:
        expression.toString() == expected

        where:
        matcherMethod     | arguments                                  | expected
        'anyByte'         | []                                         | "1 * mockedType.method(_ as ${Byte.simpleName}.class)"
        'anyChar'         | []                                         | "1 * mockedType.method(_ as ${Character.simpleName}.class)"
        'anyInt'          | []                                         | "1 * mockedType.method(_ as ${Integer.simpleName}.class)"
        'anyLong'         | []                                         | "1 * mockedType.method(_ as ${Long.simpleName}.class)"
        'anyFloat'        | []                                         | "1 * mockedType.method(_ as ${Float.simpleName}.class)"
        'anyDouble'       | []                                         | "1 * mockedType.method(_ as ${Double.simpleName}.class)"
        'anyShort'        | []                                         | "1 * mockedType.method(_ as ${Short.simpleName}.class)"
        'anyString'       | []                                         | "1 * mockedType.method(_ as ${String.simpleName}.class)"
        'anyList'         | []                                         | "1 * mockedType.method(_ as ${List.simpleName}.class)"
        'anySet'          | []                                         | "1 * mockedType.method(_ as ${Set.simpleName}.class)"
        'anyMap'          | []                                         | "1 * mockedType.method(_ as ${Map.simpleName}.class)"
        'anyCollection'   | []                                         | "1 * mockedType.method(_ as ${Collection.simpleName}.class)"
        'anyIterable'     | []                                         | "1 * mockedType.method(_ as ${Iterable.simpleName}.class)"
        'anyObject'       | []                                         | '1 * mockedType.method(_)'
        'any'             | []                                         | '1 * mockedType.method(_)'
        'isNull'          | []                                         | '1 * mockedType.method(null)'
        'isNotNull'       | []                                         | '1 * mockedType.method(!null)'
        'any'             | [typeLiteral(String)]                      | '1 * mockedType.method(_ as String.class)'
        'any'             | [typeLiteral(Object)]                      | '1 * mockedType.method(_ as Object.class)'
        'isA'             | [typeLiteral(String)]                      | '1 * mockedType.method(_ as String.class)'
        'isA'             | [typeLiteral(Object)]                      | '1 * mockedType.method(_ as Object.class)'
        'anyListOf'       | [typeLiteral(String)]                      | '1 * mockedType.method(_ as List<String>.class)'
        'anyCollectionOf' | [typeLiteral(String)]                      | '1 * mockedType.method(_ as Collection<String>.class)'
        'anyIterableOf'   | [typeLiteral(String)]                      | '1 * mockedType.method(_ as Iterable<String>.class)'
        'anySetOf'        | [typeLiteral(String)]                      | '1 * mockedType.method(_ as Set<String>.class)'
        'anyMapOf'        | [typeLiteral(String), typeLiteral(Object)] | '1 * mockedType.method(_ as Map<String,Object>.class)'
        'eq'              | [nodeFactory.simpleName('variable')]       | '1 * mockedType.method(variable)'
        'eq'              | [nodeFactory.stringLiteral('some string')] | '1 * mockedType.method("some string")'
        'eq'              | [nodeFactory.numberLiteral('13')]          | '1 * mockedType.method(13)'
    }

    private TypeLiteral typeLiteral(Class<?> clazz) {
        nodeFactory.typeLiteral(nodeFactory.simpleType(clazz.simpleName))
    }

    private MethodInvocation verifyInvocation() {
        nodeFactory.methodInvocation(VERIFY, [anObject('mockedType')])
    }

    private MethodInvocation verifyInvocation(MethodInvocation verificationMode) {
        nodeFactory.methodInvocation(VERIFY, [anObject('mockedType'), verificationMode])
    }

    private SimpleName anObject(String name) {
        nodeFactory.simpleName(name)
    }
}
