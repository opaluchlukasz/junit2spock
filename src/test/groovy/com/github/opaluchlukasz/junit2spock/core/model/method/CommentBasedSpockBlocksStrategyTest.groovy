package com.github.opaluchlukasz.junit2spock.core.model.method

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.AST
import spock.lang.Shared
import spock.lang.Specification

import static ch.qos.logback.classic.Level.WARN
import static com.github.opaluchlukasz.junit2spock.core.model.method.CommentBasedSpockBlocksStrategy.GIVEN_BLOCK_START_MARKER_METHOD
import static com.github.opaluchlukasz.junit2spock.core.model.method.CommentBasedSpockBlocksStrategy.THEN_BLOCK_START_MARKER_METHOD
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.given
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST
import static org.slf4j.LoggerFactory.getLogger

class CommentBasedSpockBlocksStrategyTest extends Specification {

    private static final AST ast = newAST(JLS8)

    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })

    private Appender<ILoggingEvent> appender = Mock(Appender)
    private Logger logger = (Logger) getLogger(CommentBasedSpockBlocksStrategy)

    def setup() {
        logger.addAppender(appender)
    }

    def cleanup() {
        logger.detachAppender(appender)
    }

    def 'should use first given as a start of the given block'() {
        given:
        def body = [nodeFactory.methodInvocation(GIVEN_BLOCK_START_MARKER_METHOD, []),
                    nodeFactory.methodInvocation('someMethod', []),
                    nodeFactory.methodInvocation(GIVEN_BLOCK_START_MARKER_METHOD, []),
                    nodeFactory.methodInvocation(THEN_BLOCK_START_MARKER_METHOD, [])]

        CommentBasedSpockBlocksStrategy commentBasedSpockBlocksStrategy = new CommentBasedSpockBlocksStrategy(body, 'testMethod')

        expect:
        commentBasedSpockBlocksStrategy.apply()

        body.size() == 3
        body.findAll {it == given()} .size() == 1
    }

    def 'should not apply if then comment not found within the body'() {
        given:
        def body = [nodeFactory.methodInvocation(GIVEN_BLOCK_START_MARKER_METHOD, []),
                    nodeFactory.methodInvocation('someMethod', []),
                    nodeFactory.methodInvocation(GIVEN_BLOCK_START_MARKER_METHOD, [])]

        CommentBasedSpockBlocksStrategy commentBasedSpockBlocksStrategy = new CommentBasedSpockBlocksStrategy(body, 'testMethod')

        expect:
        !commentBasedSpockBlocksStrategy.apply()
    }

    def 'should log inconsistency between number of when and then blocks'() {
        given:
        def body = [nodeFactory.methodInvocation(GIVEN_BLOCK_START_MARKER_METHOD, []),
                    nodeFactory.methodInvocation('someMethod', []),
                    nodeFactory.methodInvocation(THEN_BLOCK_START_MARKER_METHOD, [])]

        def testMethodName = 'testMethod'
        CommentBasedSpockBlocksStrategy commentBasedSpockBlocksStrategy = new CommentBasedSpockBlocksStrategy(body, testMethodName)

        when:
        def applied = commentBasedSpockBlocksStrategy.apply()

        then:
        applied
        1 * appender.doAppend({ LoggingEvent event ->
            event.level == WARN && event.message == "Numbers of when/then blocks do not match for test method: $testMethodName"
        } as ILoggingEvent)
    }
}
