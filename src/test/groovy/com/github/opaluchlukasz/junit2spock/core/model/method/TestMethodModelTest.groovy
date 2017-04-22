package com.github.opaluchlukasz.junit2spock.core.model.method

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodDeclaration
import org.eclipse.jdt.core.dom.MethodInvocation
import org.eclipse.jdt.core.dom.SimpleName
import spock.lang.Shared
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod
import static com.github.opaluchlukasz.junit2spock.core.model.method.CodeBasedSpockBlocksStrategy.THEN_BLOCK_START
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.expect
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.given
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.then
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.when
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR
import static org.eclipse.jdt.core.dom.AST.newAST
import static org.spockframework.util.Identifiers.THROWN

class TestMethodModelTest extends Specification {

    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory()

    def 'should return \'def\' as a method modifier'() {
        expect:
        aTestMethodModel(aMethod(newAST(AST.JLS8)).build()).methodModifier() == 'def '
    }

    def 'should return line separator as a method suffix'() {
        expect:
        aTestMethodModel(aMethod(newAST(AST.JLS8)).build()).methodSuffix() == SEPARATOR
    }

    def 'should add expect block if test has single statement'(String methodName) {
        MethodDeclaration methodDeclaration = aMethod(nodeFactory.ast)
                .withBodyExpression(nodeFactory.methodInvocation(methodName,
                [nodeFactory.numberLiteral("0"), nodeFactory.numberLiteral("0")]))
                .build()
        TestMethodModel testMethodModel = aTestMethodModel(methodDeclaration)

        expect:
        testMethodModel.body().get(0) == expect()

        where:
        methodName << THEN_BLOCK_START
    }

    def 'should add given before expect when setup performed'(String methodName) {
        given:
        MethodDeclaration methodDeclaration = aMethod(nodeFactory.ast)
                .withBodyStatement(nodeFactory.variableDeclarationStatement('object'))
                .withBodyExpression(nodeFactory.methodInvocation(methodName,
                [nodeFactory.numberLiteral("0"), nodeFactory.numberLiteral("0")]))
                .build()
        TestMethodModel testMethodModel = aTestMethodModel(methodDeclaration)

        expect:
        testMethodModel.body().get(0) == given()
        testMethodModel.body().get(2) == expect()

        where:
        methodName << THEN_BLOCK_START
    }

    def 'should add when/then when last statement before assertions is method invocation'(String methodName) {
        given:
        MethodDeclaration methodDeclaration = aMethod(nodeFactory.ast)
                .withBodyExpression(nodeFactory.methodInvocation('someMethod', []))
                .withBodyExpression(nodeFactory.methodInvocation(methodName,
                [nodeFactory.numberLiteral("0"), nodeFactory.numberLiteral("0")]))
                .build()
        TestMethodModel testMethodModel = aTestMethodModel(methodDeclaration)

        expect:
        testMethodModel.body().get(0) == when()
        testMethodModel.body().get(2) == then()

        where:
        methodName << THEN_BLOCK_START
    }

    def 'should add given before when/then when setup performed'(String methodName) {
        MethodDeclaration methodDeclaration = aMethod(nodeFactory.ast)
                .withBodyStatement(nodeFactory.variableDeclarationStatement('object'))
                .withBodyExpression(nodeFactory.methodInvocation('someMethod', []))
                .withBodyExpression(nodeFactory.methodInvocation(methodName,
                [nodeFactory.numberLiteral("0"), nodeFactory.numberLiteral("0")]))
                .build()
        TestMethodModel testMethodModel = aTestMethodModel(methodDeclaration)

        expect:
        testMethodModel.body().get(0) == given()
        testMethodModel.body().get(2) == when()
        testMethodModel.body().get(4) == then()

        where:
        methodName << THEN_BLOCK_START
    }

    def 'should add thrown method invocation when expected exception declared'() {
        given:
        def exceptionName = nodeFactory.simpleType(nodeFactory.simpleName(NullPointerException.getSimpleName()))
        def testAnnotation = nodeFactory.annotation('Test', ['expected': nodeFactory.typeLiteral(exceptionName)])
        MethodDeclaration methodDeclaration = aMethod(nodeFactory.ast)
                .withAnnotation(testAnnotation)
                .build()
        TestMethodModel testMethodModel = aTestMethodModel(methodDeclaration)

        expect:
        testMethodModel.body().get(0) == expect()
        testMethodModel.body().get(1) instanceof MethodInvocation
        MethodInvocation methodInvocation = testMethodModel.body().get(1)
        methodInvocation.name.identifier == THROWN
        methodInvocation.arguments().size() == 1
        ((SimpleName) methodInvocation.arguments().get(0)).identifier == nodeFactory.simpleName(exceptionName.name.fullyQualifiedName).identifier
    }

    def 'should return human readable test name'() {
        given:
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
