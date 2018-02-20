package com.github.opaluchlukasz.junit2spock.core.node.wrapper

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.util.TestConfig
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.BooleanLiteral
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.SimpleName
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.Applicable.FIXTURE_METHOD
import static com.github.opaluchlukasz.junit2spock.core.builder.IfStatementBuilder.anIfStatement
import static com.github.opaluchlukasz.junit2spock.core.builder.TryStatementBuilder.aTryStatement
import static com.github.opaluchlukasz.junit2spock.core.node.wrapper.WrapperDecorator.wrap
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.EQUALS

@ContextConfiguration(classes = TestConfig.class)
class WrapperDecoratorTest extends Specification {
    private static final AST ast = newAST(JLS8)
    @Shared private ASTNodeFactory nf = new ASTNodeFactory({
        get: ast
    })

    def 'should wrap statement using correct wrapper'() {
        expect:
        wrap(statement, 0, FIXTURE_METHOD).getClass() == instance

        where:
        statement                                                    | instance
        anIfStatement(ast).withExpression(equalExpression()).build() | IfStatementWrapper
        aTryStatement(ast).build()                                   | TryStatementWrapper
    }

    def 'should not wrap any other statement'() {
        given:
        def statement = new Object()

        expect:
        wrap(statement, 0, FIXTURE_METHOD) == statement
    }

    private InfixExpression equalExpression() {
        SimpleName varA = nf.simpleName('a')
        BooleanLiteral trueLiteral = nf.booleanLiteral(true)
        nf.infixExpression(EQUALS, varA, trueLiteral)
    }
}
