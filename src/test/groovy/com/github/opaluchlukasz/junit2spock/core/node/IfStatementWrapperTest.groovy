package com.github.opaluchlukasz.junit2spock.core.node

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.util.TestConfig
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.Block
import org.eclipse.jdt.core.dom.BooleanLiteral
import org.eclipse.jdt.core.dom.IfStatement
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.SimpleName
import org.eclipse.jdt.core.dom.Statement
import org.eclipse.jdt.core.dom.ThrowStatement
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.Applicable.REGULAR_METHOD
import static com.github.opaluchlukasz.junit2spock.core.builder.ClassInstanceCreationBuilder.aClassInstanceCreation
import static com.github.opaluchlukasz.junit2spock.core.builder.IfStatementBuilder.anIfStatement
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR
import static java.util.Arrays.asList
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
        IfStatement statement = anIfStatement(ast).withExpression(expression)
                .withThenStatement(thenStatement).withElseStatement(elseStatement).build()
        IfStatementWrapper ifStatementWrapper = new IfStatementWrapper(statement, 0, REGULAR_METHOD)

        expect:
        ifStatementWrapper.toString() == expected

        where:
        expression        | thenStatement            | elseStatement           | expected
        equalExpression() | block()                  | null                    | "\tif (a == true) {$SEPARATOR\t}$SEPARATOR"
        equalExpression() | null                     | block()                 | "\tif (a == true) {$SEPARATOR\t}$SEPARATOR"
        equalExpression() | block()                  | block()                 | "\tif (a == true) {$SEPARATOR\t}$SEPARATOR"
        equalExpression() | block()                  | block(throwStatement()) | "\tif (a == true) {$SEPARATOR\t} else {$SEPARATOR\t\tthrow new Foo()\n\t}$SEPARATOR"
        equalExpression() | block(throwStatement())  | null                    | "\tif (a == true) {$SEPARATOR\t\tthrow new Foo()\n\t}$SEPARATOR"
        equalExpression() | null                     | ifThenStatement()       | "\tif (a == true) {$SEPARATOR\t} else if (a == true) {$SEPARATOR\t}$SEPARATOR$SEPARATOR"
        equalExpression() | block(ifThenStatement()) | null                    | "\tif (a == true) {$SEPARATOR\t\tif (a == true) {$SEPARATOR\t\t}$SEPARATOR\t}$SEPARATOR"
    }

    private IfStatement ifThenStatement() {
        anIfStatement(ast).withExpression(equalExpression()).withThenStatement(block()).build()
    }

    private ThrowStatement throwStatement() {
        nf.throwStatement(aClassInstanceCreation(ast).withType(nf.simpleType('Foo')).build())
    }

    private static Block block(Statement... statements) {
        Block block = ast.newBlock()
        block.statements().addAll(asList(statements))
        return block
    }

    private InfixExpression equalExpression() {
        SimpleName varA = nf.simpleName('a')
        BooleanLiteral trueLiteral = nf.booleanLiteral(true)
        nf.infixExpression(EQUALS, varA, trueLiteral)
    }
}
