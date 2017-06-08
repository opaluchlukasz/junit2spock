package com.github.opaluchlukasz.junit2spock.core.node.wrapper

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.util.TestConfig
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ClassInstanceCreation
import org.eclipse.jdt.core.dom.SingleVariableDeclaration
import org.eclipse.jdt.core.dom.VariableDeclarationExpression
import org.eclipse.jdt.core.dom.VariableDeclarationFragment
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.Applicable.REGULAR_METHOD
import static com.github.opaluchlukasz.junit2spock.core.builder.TryStatementBuilder.aTryStatement
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST
import static org.eclipse.jdt.core.dom.PrefixExpression.Operator.INCREMENT

@ContextConfiguration(classes = TestConfig.class)
class TryStatementWrapperTest extends Specification {

    private static final AST ast = newAST(JLS8)
    @Shared private ASTNodeFactory nf = new ASTNodeFactory({
        get: ast
    })

    def 'should have proper toString method'() {
        given:
        TryStatementWrapper tryStatementWrapper = new TryStatementWrapper(tryStatement.build(), 0, REGULAR_METHOD)

        expect:
        tryStatementWrapper.toString() == expected

        where:
        tryStatement                                                                                | expected
        aTryStatement(ast)
                .withCatchClause(exception('Exception'), nf.returnStatement(nf.stringLiteral('a'))) | "try {$SEPARATOR\t} catch(Exception ex) {$SEPARATOR\t\treturn 'a'\n\t}$SEPARATOR"
        aTryStatement(ast)
                .withFinally(nf.returnStatement(nf.stringLiteral('a')))                             | "try {$SEPARATOR\t} finally {$SEPARATOR\t\treturn 'a'\n\t}$SEPARATOR"
        aTryStatement(ast)
                .withCatchClause(exception('IllegalStateException'), nf.returnStatement(nf.stringLiteral('a')))
                .withCatchClause(exception('Exception'), nf.returnStatement(nf.stringLiteral('b'))) | "try {$SEPARATOR\t} catch(IllegalStateException ex) {$SEPARATOR\t\treturn 'a'\n\t} catch(Exception ex) {$SEPARATOR\t\treturn 'b'\n\t}$SEPARATOR"
    }

    def 'should add resources to try block body'() {
        given:
        TryStatementWrapper tryStatementWrapper = new TryStatementWrapper(tryStatement.build(), 0, REGULAR_METHOD)

        expect:
        tryStatementWrapper.toString() == expected

        where:
        tryStatement                                                       | expected
        aTryStatement(ast)
                .withResource(variableDeclaration('BufferedReader', 'br')) | "try {$SEPARATOR\t\tBufferedReader br=new BufferedReader()$SEPARATOR\t}$SEPARATOR"
        aTryStatement(ast)
                .withBody(nf.expressionStatement(nf.prefixExpression(INCREMENT, nf.simpleName('i'))))
                .withResource(variableDeclaration('BufferedReader', 'br'))
                .withResource(variableDeclaration('InputStream', 'is'))    | "try {$SEPARATOR\t\tBufferedReader br=new BufferedReader()$SEPARATOR\t\tInputStream is=new InputStream()$SEPARATOR\t\t++i\n\t}$SEPARATOR"

    }

    private VariableDeclarationExpression variableDeclaration(String type, String name) {
        VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment()
        fragment.setName(nf.simpleName(name))
        ClassInstanceCreation creation = ast.newClassInstanceCreation()
        creation.setType(nf.simpleType(type))
        fragment.setInitializer(creation)
        VariableDeclarationExpression variableDeclarationExpression = ast.newVariableDeclarationExpression(fragment)
        variableDeclarationExpression.setType(nf.simpleType(type))
        variableDeclarationExpression
    }

    private SingleVariableDeclaration exception(String type, String name = 'ex') {
        nf.singleVariableDeclaration(nf.simpleType(type), name)
    }
}
