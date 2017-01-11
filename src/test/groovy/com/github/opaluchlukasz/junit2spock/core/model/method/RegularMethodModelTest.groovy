package com.github.opaluchlukasz.junit2spock.core.model.method

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.MethodDeclaration
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod

class RegularMethodModelTest extends Specification {

    private ASTNodeFactory nodeFactory = new ASTNodeFactory()

    def 'should return human readable test name'() {
        MethodDeclaration methodDeclaration = aMethod(nodeFactory.ast)
                .withName('someMethodName')
                .build()
        RegularMethodModel regularMethodModel = aRegularMethodModel(methodDeclaration)

        expect:
        regularMethodModel.methodName == 'someMethodName'
    }

    private static RegularMethodModel aRegularMethodModel(MethodDeclaration methodDeclaration) {
        new RegularMethodModel(methodDeclaration)
    }
}
