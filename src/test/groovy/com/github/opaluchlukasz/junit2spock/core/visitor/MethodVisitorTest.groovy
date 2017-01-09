package com.github.opaluchlukasz.junit2spock.core.visitor

import com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodDeclaration
import spock.lang.Specification

import static org.eclipse.jdt.core.dom.Modifier.ModifierKeyword.PRIVATE_KEYWORD
import static org.eclipse.jdt.core.dom.Modifier.ModifierKeyword.PROTECTED_KEYWORD
import static org.eclipse.jdt.core.dom.Modifier.ModifierKeyword.PUBLIC_KEYWORD

class MethodVisitorTest extends Specification {

    def 'should add private keyword for private method'() {
        given:
        def ast = AST.newAST(AST.JLS8)

        MethodDeclaration method = MethodDeclarationBuilder.aMethod(ast)
                .withName('dummy')
                .withModifier(modifier)
                .build()

        when:
        MethodVisitor regularMethodVisitor = new MethodVisitor()
        method.accept(regularMethodVisitor)

        then:
        regularMethodVisitor.methodModel().asGroovyMethod(0).contains('private') == expected

        where:
        modifier          | expected
        PRIVATE_KEYWORD   | true
        PROTECTED_KEYWORD | false
        PUBLIC_KEYWORD    | false
    }
}
