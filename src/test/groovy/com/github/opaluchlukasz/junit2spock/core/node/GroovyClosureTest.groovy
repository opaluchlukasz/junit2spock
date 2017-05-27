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

    def 'should return closure without arguments when none provided'() {
        given:
        Block block = nodeFactory.block(nodeFactory.expressionStatement(nodeFactory.methodInvocation('someMethod', [])))

        expect:
        new GroovyClosure(block).toString() == '{\n\t\t\tsomeMethod()\n\t\t}'
    }

    def 'should return closure with arguments'() {
        given:
        Block block = nodeFactory.block(nodeFactory.expressionStatement(nodeFactory.methodInvocation('someMethod', [])))
        GroovyClosure closure = new GroovyClosure(block, *arguments)

        expect:
        closure.toString() == expected

        where:
        arguments | expected
        [nodeFactory.singleVariableDeclaration(nodeFactory.simpleType(String.simpleName), 'a')] | '{ String a ->\n\t\t\tsomeMethod()\n\t\t}'
        [nodeFactory.singleVariableDeclaration(nodeFactory.simpleType(String.simpleName), 'a'),
         nodeFactory.singleVariableDeclaration(nodeFactory.simpleType(Integer.simpleName), 'b')] | '{ String a, Integer b ->\n\t\t\tsomeMethod()\n\t\t}'
    }
}
