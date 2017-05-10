package com.github.opaluchlukasz.junit2spock.core.feature.mockito

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ASTNode
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static ch.qos.logback.classic.Level.WARN
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST
import static org.slf4j.LoggerFactory.getLogger

class MatcherHandlerTest extends Specification {

    private static final AST ast = newAST(JLS8)
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })

    @Subject private MatcherHandler matcherHandler = new MatcherHandler(nodeFactory)

    private Appender<ILoggingEvent> appender = Mock(Appender)
    private Logger logger = (Logger) getLogger(MatcherHandler)

    def setup() {
        logger.addAppender(appender)
    }

    def cleanup() {
        logger.detachAppender(appender)
    }

    def 'should not replace eq() invocation with more than one parameter'() {
        given:
        def methodInvocation = nodeFactory.methodInvocation('eq', [nodeFactory.simpleName('var'), nodeFactory.simpleName('var2')])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        1 * appender.doAppend({ LoggingEvent event ->
            event.level == WARN && event.message == 'Unsupported eq matcher arity.'
        } as ILoggingEvent)
        expression.toString() == 'eq(var,var2)'
    }

    def 'should log warning when unsupported matcher invoked'() {
        given:
        String matcherName = 'unsupportedMatcher'
        def methodInvocation = nodeFactory.methodInvocation(matcherName, [])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        1 * appender.doAppend({ LoggingEvent event ->
            event.level == WARN && event.formattedMessage == "Unsupported Mockito matcher: $matcherName"
        } as ILoggingEvent)
        expression.toString() == "$matcherName()"
    }

    @Unroll
    def 'should replace #methodName matcher with Spock\'s wildcard'() {
        given:
        def methodInvocation = nodeFactory.methodInvocation(methodName, [])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == '_'

        where:
        methodName << ['any', 'anyObject']
    }

    @Unroll
    def 'should replace #matcherMethod matcher with Spock\'s expression'() {
        given:
        def methodInvocation = nodeFactory.methodInvocation(matcherMethod, [])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == "_ as ${clazz.simpleName}.class"

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

    def 'should replace isNull() matcher with null literal'() {
        given:
        def methodInvocation = nodeFactory.methodInvocation('isNull', [])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == 'null'
    }

    @Unroll
    def 'should replace #matcher() matcher with negated null literal'() {
        given:
        def methodInvocation = nodeFactory.methodInvocation(matcher, [])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == '!null'

        where:
        matcher << ['isNotNull', 'notNull']
    }

    @Unroll
    def 'should replace #methodName(#clazz) matcher with Spock\'s wildcard with cast'(Class<?> clazz, String methodName) {
        given:
        def methodInvocation = nodeFactory.methodInvocation(methodName,
                [nodeFactory.typeLiteral(nodeFactory.simpleType(nodeFactory.simpleName(clazz.simpleName)))])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == "_ as ${clazz.simpleName}.class"

        where:
        [clazz, methodName] << [[String, Object], ['any', 'isA']].combinations()
    }

    @Unroll
    def 'should replace #method(#clazz) matcher with Spock\'s wildcard with cast'() {
        given:
        def methodInvocation = nodeFactory
                .methodInvocation(method, [nodeFactory.typeLiteral(nodeFactory.simpleType(nodeFactory.simpleName(clazz.simpleName)))])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == expected

        where:
        method            | clazz  | expected
        'anyListOf'       | String | '_ as List<String>.class'
        'anyCollectionOf' | String | '_ as Collection<String>.class'
        'anyIterableOf'   | String | '_ as Iterable<String>.class'
        'anySetOf'        | String | '_ as Set<String>.class'
    }

    def 'should replace eq() matcher with plain expression'() {
        given:
        def methodInvocation = nodeFactory.methodInvocation('eq', [argument])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == expected

        where:
        argument                                 | expected
        nodeFactory.simpleName('variable')       | 'variable'
        nodeFactory.stringLiteral('some string') | '"some string"'
        nodeFactory.numberLiteral('13')          | '13'
    }

    def 'should return Spock\'s wildcard'() {
        expect:
        matcherHandler.wildcard().toString() == '_'
    }
}
