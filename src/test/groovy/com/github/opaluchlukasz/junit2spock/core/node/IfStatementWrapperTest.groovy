package com.github.opaluchlukasz.junit2spock.core.node

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.Block
import org.eclipse.jdt.core.dom.BooleanLiteral
import org.eclipse.jdt.core.dom.Expression
import org.eclipse.jdt.core.dom.IfStatement
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.SimpleName
import org.eclipse.jdt.core.dom.Statement
import org.eclipse.jdt.core.dom.ThrowStatement
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.opaluchlukasz.junit2spock.core.Applicable.REGULAR_METHOD
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.EQUALS

class IfStatementWrapperTest extends Specification {

    @Shared private static ASTNodeFactory nf = new ASTNodeFactory()

    @Unroll
    def 'should have proper toString method'() {
        given:
        IfStatement statement = ifStatement(expression, thenStatement, elseStatement)

        expect:
        new IfStatementWrapper(statement, 0, REGULAR_METHOD).toString() == expected

        where:
        expression        | thenStatement                                        | elseStatement                                 | expected
        infixExpression() | block()                                              | null                                          | "\tif (a == true) {$SEPARATOR\t}$SEPARATOR"
        infixExpression() | null                                                 | block()                                       | "\tif (a == true) {$SEPARATOR\t}$SEPARATOR"
        infixExpression() | block()                                              | block()                                       | "\tif (a == true) {$SEPARATOR\t}$SEPARATOR"
        infixExpression() | block()                                              | block(throwStatement())                       | "\tif (a == true) {$SEPARATOR\t} else {$SEPARATOR\t\tthrow new Foo()\n\t}$SEPARATOR"
        infixExpression() | block(throwStatement())                              | null                                          | "\tif (a == true) {$SEPARATOR\t\tthrow new Foo()\n\t}$SEPARATOR"
        infixExpression() | null                                                 | ifStatement(infixExpression(), block(), null) | "\tif (a == true) {$SEPARATOR\t} else \tif (a == true) {$SEPARATOR\t}$SEPARATOR$SEPARATOR"
        infixExpression() | block(ifStatement(infixExpression(), block(), null)) | null                                          | "\tif (a == true) {$SEPARATOR\t\tif (a == true) {$SEPARATOR\t\t}$SEPARATOR\t}$SEPARATOR"
    }

    private static ThrowStatement throwStatement() {
        nf.throwStatement(nf.classInstanceCreation(nf.simpleType(nf.simpleName('Foo'))))
    }

    private static Block block(Statement... statements) {
        def block = nf.block()
        block.statements().addAll(statements)
        block
    }

    private static IfStatement ifStatement(Expression expression, Statement thenStatement, Statement elseStatement) {
        IfStatement ifStatement = nf.ifStatement()
        ifStatement.setExpression(expression)
        if (thenStatement != null) {
            ifStatement.setThenStatement(thenStatement)
        }
        ifStatement.setElseStatement(elseStatement)
        ifStatement
    }

    private static InfixExpression infixExpression() {
        SimpleName varA = nf.simpleName('a')
        BooleanLiteral trueLiteral = nf.booleanLiteral(true)
        nf.infixExpression(EQUALS, varA, trueLiteral)
    }
}
