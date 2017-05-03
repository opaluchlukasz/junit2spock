package com.github.opaluchlukasz.junit2spock.core.model.method

import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodDeclaration
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod
import static com.github.opaluchlukasz.junit2spock.core.model.method.MethodDeclarationHelper.isPrivate
import static com.github.opaluchlukasz.junit2spock.core.model.method.MethodDeclarationHelper.isTestMethod
import static org.eclipse.jdt.core.dom.AST.*
import static org.eclipse.jdt.core.dom.Modifier.ModifierKeyword.PRIVATE_KEYWORD

class MethodDeclarationHelperTest extends Specification {

    def 'should return true for method with private modifier'() {
        given:
        MethodDeclaration methodDeclaration = aMethod(newAST(JLS8)).withModifier(PRIVATE_KEYWORD).build()

        expect:
        isPrivate(methodDeclaration)
    }

    def 'should return true for method annotated with @Test'() {
        given:
        AST ast = newAST(JLS8)
        def testAnnotation = ast.newMarkerAnnotation()
        testAnnotation.setTypeName(ast.newName('Test'))
        MethodDeclaration methodDeclaration = aMethod(ast).withAnnotation(testAnnotation).build()

        expect:
        isTestMethod(methodDeclaration)
    }

    def 'should return false for non test method'() {
        given:
        MethodDeclaration methodDeclaration = aMethod(newAST(JLS8)).build()

        expect:
        !isTestMethod(methodDeclaration)
    }
}
