package com.github.opaluchlukasz.junit2spock.core.feature.mockito

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.AstProvider
import com.github.opaluchlukasz.junit2spock.core.node.GroovyClosureFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.ClassInstanceCreation
import org.eclipse.jdt.core.dom.TypeLiteral
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static ch.qos.logback.classic.Level.WARN
import static com.github.opaluchlukasz.junit2spock.core.builder.ClassInstanceCreationBuilder.aClassInstanceCreationBuilder
import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.GREATER
import static org.slf4j.LoggerFactory.getLogger

class MatcherHandlerTest extends Specification {

    private static final AST ast = newAST(JLS8)
    private static final AstProvider AST_PROVIDER = {
        get: ast
    }
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory(AST_PROVIDER)
    @Shared private GroovyClosureFactory groovyClosureFactory = new GroovyClosureFactory(AST_PROVIDER)

    @Subject private MatcherHandler matcherHandler = new MatcherHandler(nodeFactory, groovyClosureFactory)

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
            event.level == WARN && event.formattedMessage == 'Unsupported eq matcher arity.'
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
        def methodInvocation = nodeFactory.methodInvocation(methodName, [typeLiteral(clazz)])

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
        def methodInvocation = nodeFactory.methodInvocation(method, [typeLiteral(clazz)])

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

    def 'should replace anyMap matcher with Spock\'s wildcard with cast'() {
        given:
        def methodInvocation = nodeFactory.methodInvocation('anyMapOf', [typeLiteral(String), typeLiteral(Integer)])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == '_ as Map<String,Integer>.class'
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

    def 'should replace startsWith matcher with closure with cast'() {
        given:
        def methodInvocation = nodeFactory.methodInvocation('startsWith', [nodeFactory.stringLiteral('some string')])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == '{\n\t\t\tit.startsWith(\'some string\')\n\t\t} as String.class'
    }

    def 'should replace intThat matcher using anonymous ArgumentMatcher with closure with a cast'() {
        given:
        def methodInvocation = nodeFactory.methodInvocation('intThat',
                [anonymousArgumentMatcherClass(Integer, aMethod(ast)
                        .withName('matches')
                        .withParameter(nodeFactory.singleVariableDeclaration(nodeFactory.simpleType(Integer.simpleName), 'a'))
                        .withBodyStatement(nodeFactory.returnStatement(nodeFactory.infixExpression(GREATER, nodeFactory.simpleName('a'), nodeFactory.numberLiteral('13'))))
                        .build())])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == '{ Integer a ->\n\t\t\treturn a > 13\n\t\t} as Integer.class'
    }

    def 'should replace argThat matcher using anonymous ArgumentMatcher with closure with a cast'() {
        given:
        def methodInvocation = nodeFactory.methodInvocation('argThat',
                [anonymousArgumentMatcherClass(List, aMethod(ast)
                        .withName('matches')
                        .withParameter(nodeFactory.singleVariableDeclaration(nodeFactory.simpleType(List.simpleName), 'a'))
                        .withBodyStatement(nodeFactory.returnStatement(nodeFactory.methodInvocation('isEmpty', [], nodeFactory.simpleName('a'))))
                        .build())])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == '{ List a ->\n\t\t\treturn a.isEmpty()\n\t\t} as List.class'
    }

    def 'should return Spock\'s wildcard'() {
        expect:
        matcherHandler.wildcard().toString() == '_'
    }

    private ClassInstanceCreation anonymousArgumentMatcherClass(Class<?> clazz, ASTNode bodyDeclaration) {
        aClassInstanceCreationBuilder(ast)
                .withType(nodeFactory.parameterizedType(nodeFactory.simpleType('ArgumentMatcher'), [nodeFactory.simpleType(clazz.simpleName)]))
                .withBodyDeclaration(bodyDeclaration)
                .build()
    }

    private TypeLiteral typeLiteral(Class<?> clazz) {
        nodeFactory.typeLiteral(nodeFactory.simpleType(clazz.simpleName))
    }
}
