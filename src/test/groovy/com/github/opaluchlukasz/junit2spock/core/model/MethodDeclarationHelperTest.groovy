package com.github.opaluchlukasz.junit2spock.core.model

import com.github.opaluchlukasz.junit2spock.core.builder.MethodBuilder
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodDeclaration
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.model.MethodDeclarationHelper.isPrivate
import static com.github.opaluchlukasz.junit2spock.core.model.MethodDeclarationHelper.isTestMethod
import static org.eclipse.jdt.core.dom.Modifier.ModifierKeyword.PRIVATE_KEYWORD

class MethodDeclarationHelperTest extends Specification {

    def 'should return true for method with private modifier'() {
        given:
        MethodDeclaration methodDeclaration = MethodBuilder.aMethod(AST.newAST(AST.JLS8)).withModifier(PRIVATE_KEYWORD).build()

        expect:
        isPrivate(methodDeclaration)
    }

    def 'should return true for method annotated with @Test'() {
        given:
        AST ast = AST.newAST(AST.JLS8)
        def testAnnotation = ast.newMarkerAnnotation()
        testAnnotation.setTypeName(ast.newName('Test'))
        MethodDeclaration methodDeclaration = MethodBuilder.aMethod(ast).withAnnotation(testAnnotation).build()

        expect:
        isTestMethod(methodDeclaration)
    }

    def 'should return false for non test method'() {
        given:
        MethodDeclaration methodDeclaration = MethodBuilder.aMethod(AST.newAST(AST.JLS8)).build()

        expect:
        !isTestMethod(methodDeclaration)
    }
}
