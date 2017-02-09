package com.github.opaluchlukasz.junit2spock.core.model.method

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.MethodDeclaration
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod

class FixtureMethodModelTest extends Specification {

    private ASTNodeFactory nodeFactory = new ASTNodeFactory()

    def 'should return provided test name'() {
        given:
        MethodDeclaration methodDeclaration = aMethod(nodeFactory.ast)
                .withName('someMethodName')
                .build()
        FixtureMethodModel regularMethodModel = fixtureMethodModel(methodDeclaration, 'setup')

        expect:
        regularMethodModel.methodName == 'setup'
    }

    def 'should return empty string as a method suffix'() {
        expect:
        fixtureMethodModel(aMethod(nodeFactory.ast).build(), 'setup').methodSuffix() == ''
    }

    def 'should return \'def\' as a method modifier'() {
        expect:
        fixtureMethodModel(aMethod(nodeFactory.ast).build(), 'setup').methodModifier().get() == 'def'
    }

    private static FixtureMethodModel fixtureMethodModel(MethodDeclaration methodDeclaration, String name) {
        new FixtureMethodModel(methodDeclaration, name)
    }
}
