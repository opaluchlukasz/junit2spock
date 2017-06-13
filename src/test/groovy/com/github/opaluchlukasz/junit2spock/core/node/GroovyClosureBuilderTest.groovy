package com.github.opaluchlukasz.junit2spock.core.node

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.AstProvider
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.SingleVariableDeclaration
import org.eclipse.jdt.core.dom.Statement
import org.eclipse.jdt.core.dom.TypeLiteral
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST

class GroovyClosureBuilderTest extends Specification {

    private static final AST ast = newAST(JLS8)
    private static final AstProvider AST_PROVIDER = {
        get: ast
    }
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory(AST_PROVIDER)

    @Subject private GroovyClosureBuilder groovyClosureBuilder = new GroovyClosureBuilder(AST_PROVIDER, nodeFactory)

    def 'should create proper groovy closure'() {
        given:
        Statement statement = nodeFactory.expressionStatement(nodeFactory.methodInvocation('someMethod', []))
        TypeLiteral typeLiteral = nodeFactory.typeLiteral(nodeFactory.simpleType('Boolean'))
        SingleVariableDeclaration argument = nodeFactory.singleVariableDeclaration(nodeFactory.simpleType('Boolean'), 'a')

        when:
        GroovyClosure groovyClosure = groovyClosureBuilder.aClosure()
                .withBodyStatement(statement)
                .withTypeLiteral(typeLiteral)
                .withArgument(argument)
                .build()

        then:
        groovyClosure.toString() == "{ Boolean a ->$SEPARATOR\t\t\tsomeMethod()\n\t\t} as Boolean.class"

    }
}
