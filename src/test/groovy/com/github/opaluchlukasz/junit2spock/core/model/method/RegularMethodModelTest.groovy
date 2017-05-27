package com.github.opaluchlukasz.junit2spock.core.model.method

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.AstProvider
import com.github.opaluchlukasz.junit2spock.core.util.TestConfig
import org.eclipse.jdt.core.dom.MethodDeclaration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod
import static org.eclipse.jdt.core.dom.Modifier.ModifierKeyword.PRIVATE_KEYWORD
import static org.eclipse.jdt.core.dom.Modifier.ModifierKeyword.STATIC_KEYWORD

@ContextConfiguration(classes = TestConfig.class)
class RegularMethodModelTest extends Specification {

    @Autowired private ASTNodeFactory nodeFactory
    @Autowired private AstProvider astProvider

    def 'should return unchanged test name'() {
        given:
        MethodDeclaration methodDeclaration = aMethod(astProvider.get())
                .withName('someMethodName')
                .build()
        RegularMethodModel regularMethodModel = aRegularMethodModel(methodDeclaration)

        expect:
        regularMethodModel.methodName == 'someMethodName'
    }

    def 'should return empty string as a method suffix'() {
        expect:
        aRegularMethodModel(aMethod(astProvider.get()).build()).methodSuffix() == ''
    }

    def 'should return method modifier'() {
        given:
        def methodDeclarationBuilder = aMethod(astProvider.get())
        modifiers.each { methodDeclarationBuilder.withModifier(it) }
        annotations.each { methodDeclarationBuilder.withAnnotation(nodeFactory.markerAnnotation(it, [:])) }
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
