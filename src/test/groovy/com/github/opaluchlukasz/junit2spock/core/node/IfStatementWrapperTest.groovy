package com.github.opaluchlukasz.junit2spock.core.node

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.util.TestConfig
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.Block
import org.eclipse.jdt.core.dom.BooleanLiteral
import org.eclipse.jdt.core.dom.Expression
import org.eclipse.jdt.core.dom.IfStatement
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.SimpleName
import org.eclipse.jdt.core.dom.Statement
import org.eclipse.jdt.core.dom.ThrowStatement
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.Applicable.REGULAR_METHOD
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.EQUALS

@ContextConfiguration(classes = TestConfig.class)
class IfStatementWrapperTest extends Specification {

    private static final AST ast = newAST(JLS8)
    @Shared private ASTNodeFactory nf = new ASTNodeFactory({
        get: ast
    })

    def 'should have proper toString method'() {
        given:
        IfStatement statement = ifStatement(expression, thenStatement, elseStatement)
        IfStatementWrapper ifStatementWrapper = new IfStatementWrapper(statement, 0, REGULAR_METHOD)

        expect:
        ifStatementWrapper.toString() == expected

        where:
        expression        | thenStatement                                        | elseStatement                                 | expected
        infixExpression() | block()                                              | null                                          | "\tif (a == true) {$SEPARATOR\t}$SEPARATOR"
        infixExpression() | null                                                 | block()                                       | "\tif (a == true) {$SEPARATOR\t}$SEPARATOR"
        infixExpression() | block()                                              | block()                                       | "\tif (a == true) {$SEPARATOR\t}$SEPARATOR"
        infixExpression() | block()                                              | block(throwStatement())                       | "\tif (a == true) {$SEPARATOR\t} else {$SEPARATOR\t\tthrow new Foo()\n\t}$SEPARATOR"
        infixExpression() | block(throwStatement())                              | null                                          | "\tif (a == true) {$SEPARATOR\t\tthrow new Foo()\n\t}$SEPARATOR"
        infixExpression() | null                                                 | ifStatement(infixExpression(), block(), null) | "\tif (a == true) {$SEPARATOR\t} else if (a == true) {$SEPARATOR\t}$SEPARATOR$SEPARATOR"
        infixExpression() | block(ifStatement(infixExpression(), block(), null)) | null                                          | "\tif (a == true) {$SEPARATOR\t\tif (a == true) {$SEPARATOR\t\t}$SEPARATOR\t}$SEPARATOR"
    }

    private ThrowStatement throwStatement() {
        nf.throwStatement(nf.classInstanceCreation(nf.simpleType(nf.simpleName('Foo'))))
    }

    private Block block(Statement... statements) {
        nf.block(statements)
    }

    private IfStatement ifStatement(Expression expression, Statement thenStatement, Statement elseStatement) {
        IfStatement ifStatement = nf.ifStatement()
        ifStatement.setExpression(expression)
        if (thenStatement != null) {
            ifStatement.setThenStatement(thenStatement)
        }
        ifStatement.setElseStatement(elseStatement)
        ifStatement
    }

    private InfixExpression infixExpression() {
        SimpleName varA = nf.simpleName('a')
        BooleanLiteral trueLiteral = nf.booleanLiteral(true)
        nf.infixExpression(EQUALS, varA, trueLiteral)
    }
}
