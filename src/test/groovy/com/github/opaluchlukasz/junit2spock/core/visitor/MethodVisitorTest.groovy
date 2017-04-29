package com.github.opaluchlukasz.junit2spock.core.visitor

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder
import com.github.opaluchlukasz.junit2spock.core.model.method.MethodModelFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodDeclaration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST
import static org.eclipse.jdt.core.dom.Modifier.ModifierKeyword.PRIVATE_KEYWORD
import static org.eclipse.jdt.core.dom.Modifier.ModifierKeyword.PROTECTED_KEYWORD
import static org.eclipse.jdt.core.dom.Modifier.ModifierKeyword.PUBLIC_KEYWORD

class MethodVisitorTest extends Specification {

    private static final AST ast = newAST(JLS8)
    @Subject @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })
    private MethodModelFactory methodModelFactory = new MethodModelFactory(nodeFactory)

    def 'should add private keyword for private method'() {
        given:
        MethodDeclaration method = MethodDeclarationBuilder.aMethod(ast)
                .withName('dummy')
                .withModifier(modifier)
                .build()

        when:
        MethodVisitor regularMethodVisitor = new MethodVisitor(methodModelFactory)
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
