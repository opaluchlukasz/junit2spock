package com.github.opaluchlukasz.junit2spock.core.builder

import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.Block
import org.eclipse.jdt.core.dom.Statement

class BlockBuilder {

    private final AST ast
    private List<Statement> statements = new LinkedList<>()

    static BlockBuilder aBlock(AST ast) {
        new BlockBuilder(ast)
    }

    private BlockBuilder(AST ast) {
        this.ast = ast
    }

    BlockBuilder withStatement(Statement statement) {
        statements << statement
        this
    }

    Block build() {
        Block block =  ast.newBlock()
        block.statements().addAll(statements)
        block
    }
}
