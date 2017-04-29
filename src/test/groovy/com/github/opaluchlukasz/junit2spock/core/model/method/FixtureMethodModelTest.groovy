package com.github.opaluchlukasz.junit2spock.core.model.method

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodDeclaration
import spock.lang.Shared
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR
import static org.eclipse.jdt.core.dom.AST.JLS8

class FixtureMethodModelTest extends Specification {

    private static final AST ast = AST.newAST(JLS8)
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })

    def 'should return provided test name'() {
        given:
        MethodDeclaration methodDeclaration = aMethod(ast)
                .withName('someMethodName')
                .build()
        FixtureMethodModel regularMethodModel = fixtureMethodModel(methodDeclaration, 'setup')

        expect:
        regularMethodModel.methodName == 'setup'
    }

    def 'should return new line as a method suffix'() {
        expect:
        fixtureMethodModel(aMethod(ast).build(), 'setup').methodSuffix() == SEPARATOR
    }

    def 'should return \'def\' as a method modifier'() {
        expect:
        fixtureMethodModel(aMethod(ast).build(), 'setup').methodModifier() == 'def '
    }

    private FixtureMethodModel fixtureMethodModel(MethodDeclaration methodDeclaration, String name) {
        new FixtureMethodModel(nodeFactory, methodDeclaration, name)
    }
}
