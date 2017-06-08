package com.github.opaluchlukasz.junit2spock.core.builder

import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.Block
import org.eclipse.jdt.core.dom.CatchClause
import org.eclipse.jdt.core.dom.SingleVariableDeclaration
import org.eclipse.jdt.core.dom.Statement
import org.eclipse.jdt.core.dom.TryStatement

import static com.github.opaluchlukasz.junit2spock.core.builder.BlockBuilder.aBlock

class TryStatementBuilder {

    private final AST ast
    private List<Statement> body = new LinkedList<>()
    private List<ASTNode> resources = new LinkedList<>()
    private List<CatchClause> catchClauses = new LinkedList<>()
    private Block finallyBlock

    static TryStatementBuilder aTryStatement(AST ast) {
        new TryStatementBuilder(ast)
    }

    private TryStatementBuilder(AST ast) {
        this.ast = ast
    }

    TryStatementBuilder withCatchClause(SingleVariableDeclaration exception, Statement... statements) {
        CatchClause catchClause = ast.newCatchClause()
        catchClause.setException(exception)
        catchClause.setBody(blockFrom(statements))
        this.catchClauses << catchClause
        this
    }

    TryStatementBuilder withFinally(Statement... statements) {
        this.finallyBlock = blockFrom(statements)
        this
    }

    TryStatementBuilder withBody(Statement... statements) {
        this.body.addAll(statements)
        this
    }

    TryStatementBuilder withResource(ASTNode resource) {
        this.resources << resource
        this
    }

    TryStatement build() {
        TryStatement tryStatement =  ast.newTryStatement()
        tryStatement.body.statements().addAll(body)
        tryStatement.catchClauses().addAll(catchClauses)
        tryStatement.resources().addAll(resources)
        if (finallyBlock != null) {
            tryStatement.setFinally(finallyBlock)
        }
        tryStatement
    }

    private Block blockFrom(Statement... statements) {
        BlockBuilder blockBuilder = aBlock(ast)
        statements.each { blockBuilder.withStatement(it) }
        blockBuilder.build()
    }
}
