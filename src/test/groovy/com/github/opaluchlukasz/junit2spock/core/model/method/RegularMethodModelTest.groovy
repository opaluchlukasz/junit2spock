package com.github.opaluchlukasz.junit2spock.core.model.method

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodDeclaration
import spock.lang.Shared
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST
import static org.eclipse.jdt.core.dom.Modifier.ModifierKeyword.PRIVATE_KEYWORD
import static org.eclipse.jdt.core.dom.Modifier.ModifierKeyword.STATIC_KEYWORD

class RegularMethodModelTest extends Specification {

    private static final AST ast = newAST(JLS8)

    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })

    def 'should return unchanged test name'() {
        given:
        MethodDeclaration methodDeclaration = aMethod(ast)
                .withName('someMethodName')
                .build()
        RegularMethodModel regularMethodModel = aRegularMethodModel(methodDeclaration)

        expect:
        regularMethodModel.methodName == 'someMethodName'
    }

    def 'should return empty string as a method suffix'() {
        expect:
        aRegularMethodModel(aMethod(ast).build()).methodSuffix() == ''
    }

    def 'should return method modifier'() {
        given:
        def methodDeclarationBuilder = aMethod(ast)
        modifiers.each { methodDeclarationBuilder.withModifier(it) }
        annotations.each { methodDeclarationBuilder.withAnnotation(nodeFactory.annotation(it, [:])) }
        def methodDeclaration = methodDeclarationBuilder.build()

        expect:
        aRegularMethodModel(methodDeclaration).methodModifier() == expected

        where:
        modifiers                         | annotations  | expected
        []                                | []                               | ''
        []                                | ['Override']                     | '@Override '
        [PRIVATE_KEYWORD]                 | []                               | 'private '
        [PRIVATE_KEYWORD]                 | ['Override']                     | 'private @Override '
        [PRIVATE_KEYWORD, STATIC_KEYWORD] | []                               | 'private static '
        []                                | ['Override', 'SuppressWarnings'] | '@Override @SuppressWarnings '
    }

    private RegularMethodModel aRegularMethodModel(MethodDeclaration methodDeclaration) {
        new RegularMethodModel(nodeFactory, methodDeclaration)
    }
}
