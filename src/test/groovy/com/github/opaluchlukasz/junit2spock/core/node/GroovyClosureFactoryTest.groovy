package com.github.opaluchlukasz.junit2spock.core.node

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.AstProvider
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.Expression
import org.eclipse.jdt.core.dom.Statement
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST

class GroovyClosureFactoryTest extends Specification {

    private static final AST ast = newAST(JLS8)
    private static final AstProvider AST_PROVIDER = {
        get: ast
    }
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory(AST_PROVIDER)

    @Subject private  GroovyClosureFactory groovyClosureFactory = new GroovyClosureFactory(AST_PROVIDER, nodeFactory)

    def 'should return an expression'() {
        expect:
        groovyClosureFactory.create([]) instanceof Expression
    }

    def 'should use GroovyClosure toString representation'() {
        given:
        List<Statement> statements = [nodeFactory.expressionStatement(nodeFactory.methodInvocation('someMethod', []))]

        expect:
        groovyClosureFactory.create(statements).toString() == new GroovyClosure(nodeFactory, statements).toString()
    }
}
