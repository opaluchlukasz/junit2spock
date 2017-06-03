package com.github.opaluchlukasz.junit2spock.core.builder

import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.Expression
import org.eclipse.jdt.core.dom.IfStatement
import org.eclipse.jdt.core.dom.Statement

class IfStatementBuilder {

    private final AST ast
    private Expression expression
    private Statement thenStatement
    private Statement elseStatement

    static IfStatementBuilder anIfStatement(AST ast) {
        new IfStatementBuilder(ast)
    }

    private IfStatementBuilder(AST ast) {
        this.ast = ast
    }

    IfStatementBuilder withExpression(Expression expression) {
        this.expression = expression
        this
    }

    IfStatementBuilder withThenStatement(Statement thenStatement) {
        this.thenStatement = thenStatement
        this
    }

    IfStatementBuilder withElseStatement(Statement elseStatement) {
        this.elseStatement = elseStatement
        this
    }

    IfStatement build() {
        IfStatement ifStatement =  ast.newIfStatement()
        ifStatement.setExpression(expression)
        if (thenStatement != null) {
            ifStatement.setThenStatement(thenStatement)
        }
        ifStatement.setElseStatement(elseStatement)
        ifStatement
    }
}
