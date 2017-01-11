package com.github.opaluchlukasz.junit2spock.core.model.method

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodDeclaration
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.expect
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.given
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.then
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.when
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

    def 'should add given before expect when setup performed'() {
        MethodDeclaration methodDeclaration = aMethod(nodeFactory.ast)
                .withBodyStatement(nodeFactory.variableDeclarationStatement('object'))
                .withBodyExpression(nodeFactory.methodInvocation('assertEquals'))
                .build()
        TestMethodModel testMethodModel = aTestMethodModel(methodDeclaration)

        expect:
        testMethodModel.body().size() == 4
        testMethodModel.body().get(0) == given()
        testMethodModel.body().get(2) == expect()
    }

    def 'should add when/then when last statement before assertions is method invocation'() {
        MethodDeclaration methodDeclaration = aMethod(nodeFactory.ast)
                .withBodyExpression(nodeFactory.methodInvocation('someMethod'))
                .withBodyExpression(nodeFactory.methodInvocation('assertEquals'))
                .build()
        TestMethodModel testMethodModel = aTestMethodModel(methodDeclaration)

        expect:
        testMethodModel.body().size() == 4
        testMethodModel.body().get(0) == when()
        testMethodModel.body().get(2) == then()
    }

    def 'should add given before when/then when setup performed'() {
        MethodDeclaration methodDeclaration = aMethod(nodeFactory.ast)
                .withBodyStatement(nodeFactory.variableDeclarationStatement('object'))
                .withBodyExpression(nodeFactory.methodInvocation('someMethod'))
                .withBodyExpression(nodeFactory.methodInvocation('assertEquals'))
                .build()
        TestMethodModel testMethodModel = aTestMethodModel(methodDeclaration)

        expect:
        testMethodModel.body().size() == 6
        testMethodModel.body().get(0) == given()
        testMethodModel.body().get(2) == when()
        testMethodModel.body().get(4) == then()
    }

    def 'should return human readable test name'() {
        MethodDeclaration methodDeclaration = aMethod(nodeFactory.ast)
                .withName('shouldReturnTrueWhenConditionIsMet')
                .build()
        TestMethodModel testMethodModel = aTestMethodModel(methodDeclaration)

        expect:
        testMethodModel.methodName == "'should return true when condition is met'"
    }

    private static TestMethodModel aTestMethodModel(MethodDeclaration methodDeclaration) {
        new TestMethodModel(methodDeclaration)
    }
}
