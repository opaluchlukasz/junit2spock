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
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.EQUALS
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.GREATER
import static org.slf4j.LoggerFactory.getLogger

class MatcherHandlerTest extends Specification {

    private static final AST ast = newAST(JLS8)
    private static final AstProvider AST_PROVIDER = {
        get: ast
    }
    @Shared private ASTNodeFactory nf = new ASTNodeFactory(AST_PROVIDER)
    @Shared private GroovyClosureFactory groovyClosureFactory = new GroovyClosureFactory(AST_PROVIDER)

    @Subject private MatcherHandler matcherHandler = new MatcherHandler(nf, groovyClosureFactory)

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
        def methodInvocation = nf.methodInvocation('eq', [nf.simpleName('var'), nf.simpleName('var2')])

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
        def methodInvocation = nf.methodInvocation(matcherName, [])

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
        def methodInvocation = nf.methodInvocation(methodName, [])

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
        def methodInvocation = nf.methodInvocation(matcherMethod, [])

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
        def methodInvocation = nf.methodInvocation('isNull', [])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == 'null'
    }

    @Unroll
    def 'should replace #matcher() matcher with negated null literal'() {
        given:
        def methodInvocation = nf.methodInvocation(matcher, [])

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
        def methodInvocation = nf.methodInvocation(methodName, [typeLiteral(clazz)])

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
        def methodInvocation = nf.methodInvocation(method, [typeLiteral(clazz)])

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
        def methodInvocation = nf.methodInvocation('anyMapOf', [typeLiteral(String), typeLiteral(Integer)])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == '_ as Map<String,Integer>.class'
    }

    def 'should replace eq() matcher with plain expression'() {
        given:
        def methodInvocation = nf.methodInvocation('eq', [argument])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == expected

        where:
        argument                        | expected
        nf.simpleName('variable')       | 'variable'
        nf.stringLiteral('some string') | '"some string"'
        nf.numberLiteral('13')          | '13'
    }

    @Unroll
    def 'should replace #methodName matcher with closure with cast'() {
        given:
        def methodInvocation = nf.methodInvocation(methodName, [nf.stringLiteral('some string')])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == expected

        where:
        methodName   | expected
        'contains'   | '{\n\t\t\tit.contains(\'some string\')\n\t\t} as String.class'
        'endsWith'   | '{\n\t\t\tit.endsWith(\'some string\')\n\t\t} as String.class'
        'startsWith' | '{\n\t\t\tit.startsWith(\'some string\')\n\t\t} as String.class'
    }

    @Unroll
    def 'should replace #methodName matcher using anonymous ArgumentMatcher with closure with a cast'() {
        given:
        def methodInvocation = nf.methodInvocation(methodName,
                [anonymousArgumentMatcherClass(type, aMethod(ast)
                        .withName('matches')
                        .withParameter(nf.singleVariableDeclaration(nf.simpleType(type.simpleName), 'a'))
                        .withBodyStatement(nf.returnStatement(bodyStatement))
                        .build())])

        when:
        ASTNode expression = matcherHandler.applyMatchers(methodInvocation)

        then:
        expression.toString() == expected

        where:
        methodName    | type      | bodyStatement                                                            | expected
        'argThat'     | List      | nf.methodInvocation('isEmpty', [], nf.simpleName('a'))                   | '{ List a ->\n\t\t\treturn a.isEmpty()\n\t\t} as List.class'
        'booleanThat' | Boolean   | nf.infixExpression(EQUALS, nf.simpleName('a'), nf.booleanLiteral(false)) | '{ Boolean a ->\n\t\t\treturn a == false\n\t\t} as Boolean.class'
        'byteThat'    | Byte      | nf.infixExpression(EQUALS, nf.simpleName('a'), nf.numberLiteral('0'))    | '{ Byte a ->\n\t\t\treturn a == 0\n\t\t} as Byte.class'
        'charThat'    | Character | nf.infixExpression(EQUALS, nf.simpleName('a'), nf.numberLiteral('0'))    | '{ Character a ->\n\t\t\treturn a == 0\n\t\t} as Character.class'
        'doubleThat'  | Double    | nf.infixExpression(EQUALS, nf.simpleName('a'), nf.numberLiteral('0d'))   | '{ Double a ->\n\t\t\treturn a == 0d\n\t\t} as Double.class'
        'floatThat'   | Float     | nf.infixExpression(EQUALS, nf.simpleName('a'), nf.numberLiteral('0f'))   | '{ Float a ->\n\t\t\treturn a == 0f\n\t\t} as Float.class'
        'intThat'     | Integer   | nf.infixExpression(GREATER, nf.simpleName('a'), nf.numberLiteral('13'))  | '{ Integer a ->\n\t\t\treturn a > 13\n\t\t} as Integer.class'
        'longThat'    | Long      | nf.infixExpression(EQUALS, nf.simpleName('a'), nf.numberLiteral('0l'))   | '{ Long a ->\n\t\t\treturn a == 0l\n\t\t} as Long.class'
        'shortThat'   | Short     | nf.infixExpression(EQUALS, nf.simpleName('a'), nf.numberLiteral('0'))    | '{ Short a ->\n\t\t\treturn a == 0\n\t\t} as Short.class'
    }

    def 'should return Spock\'s wildcard'() {
        expect:
        matcherHandler.wildcard().toString() == '_'
    }

    private ClassInstanceCreation anonymousArgumentMatcherClass(Class<?> clazz, ASTNode bodyDeclaration) {
        aClassInstanceCreationBuilder(ast)
                .withType(nf.parameterizedType(nf.simpleType('ArgumentMatcher'), [nf.simpleType(clazz.simpleName)]))
                .withBodyDeclaration(bodyDeclaration)
                .build()
    }

    private TypeLiteral typeLiteral(Class<?> clazz) {
        nf.typeLiteral(nf.simpleType(clazz.simpleName))
    }
}
