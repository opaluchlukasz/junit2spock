package com.github.opaluchlukasz.junit2spock.core.node

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.AstProvider
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.Block
import spock.lang.Shared
import spock.lang.Specification

import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST

class GroovyClosureTest extends Specification {

    private static final AST ast = newAST(JLS8)
    private static final AstProvider AST_PROVIDER = {
        get: ast
    }
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory(AST_PROVIDER)

    def 'should have proper toString representation'() {
        given:
        Block block = nodeFactory.block()
        block.statements().add(nodeFactory.expressionStatement(nodeFactory.methodInvocation('someMethod', [])))

        expect:
        new GroovyClosure(block).toString() == '{\n\t\t\tsomeMethod()\n\t\t}'
    }
}
