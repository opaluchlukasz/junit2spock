package com.github.opaluchlukasz.junit2spock.core.model.method

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodDeclaration
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.expect
import static org.eclipse.jdt.core.dom.AST.newAST

class TestMethodModelTest extends Specification {

    private ASTNodeFactory nodeFactory = new ASTNodeFactory()

    def 'should return \'def\' as a method modifier'() {
        expect:
        aTestMethodModel(aMethod(newAST(AST.JLS8)).build()).methodModifier() == 'def '
    }

    def 'should add expect block if test has single statement'() {

        MethodDeclaration methodDeclaration = aMethod(nodeFactory.ast)
                .withBodyExpression(nodeFactory.methodInvocation('assertEquals'))
                .build()
        TestMethodModel testMethodModel = aTestMethodModel(methodDeclaration)

        expect:
        testMethodModel.body().size() == 2
        testMethodModel.body().get(0) == expect()
    }

    private static TestMethodModel aTestMethodModel(MethodDeclaration methodDeclaration) {
        new TestMethodModel(methodDeclaration)
    }
}
