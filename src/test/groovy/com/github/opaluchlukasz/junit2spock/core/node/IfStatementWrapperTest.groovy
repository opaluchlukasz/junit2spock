package com.github.opaluchlukasz.junit2spock.core.node

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.Block
import org.eclipse.jdt.core.dom.BooleanLiteral
import org.eclipse.jdt.core.dom.IfStatement
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.SimpleName
import org.eclipse.jdt.core.dom.Statement
import org.eclipse.jdt.core.dom.ThrowStatement
import spock.lang.Shared
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.EQUALS

class IfStatementWrapperTest extends Specification {

    @Shared private static ASTNodeFactory nf = new ASTNodeFactory()

    def 'should have proper toString method'() {
        given:
        IfStatement ifStatement = nf.ifStatement()
        ifStatement.setExpression(expression)
        if (thenStatement != null) {
            ifStatement.setThenStatement(thenStatement)
        }
        ifStatement.setElseStatement(elseStatement)

        expect:
        new IfStatementWrapper(ifStatement, 0).toString() == expected

        where:
        expression        | thenStatement           | elseStatement           | expected
        infixExpression() | block()                 | null                    | "\tif (a == true) {$SEPARATOR\t}$SEPARATOR"
        infixExpression() | null                    | block()                 | "\tif (a == true) {$SEPARATOR\t}$SEPARATOR"
        infixExpression() | block()                 | block()                 | "\tif (a == true) {$SEPARATOR\t}$SEPARATOR"
        infixExpression() | block()                 | block(throwStatement()) | "\tif (a == true) {$SEPARATOR\t} else {$SEPARATOR\t\tthrow new Foo();\n\t}$SEPARATOR"
        infixExpression() | block(throwStatement()) | null                    | "\tif (a == true) {$SEPARATOR\t\tthrow new Foo();\n\t}$SEPARATOR"
    }

    private static ThrowStatement throwStatement() {
        nf.throwStatement(nf.classInstanceCreation(nf.simpleType(nf.simpleName('Foo'))))
    }

    private static Block block(Statement... statements) {
        def block = nf.block()
        block.statements().addAll(statements)
        block
    }

    private static InfixExpression infixExpression() {
        SimpleName varA = nf.simpleName('a')
        BooleanLiteral trueLiteral = nf.booleanLiteral(true)
        nf.infixExpression(EQUALS, varA, trueLiteral)
    }
}
