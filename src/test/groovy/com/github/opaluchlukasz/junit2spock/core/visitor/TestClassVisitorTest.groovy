package com.github.opaluchlukasz.junit2spock.core.visitor

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import spock.lang.Specification

class TestClassVisitorTest extends Specification {

    private ASTNodeFactory astNodeFactory = new ASTNodeFactory()

    def 'should add import to class model builder'() {
        given:
        TestClassVisitor testClassVisitor = new TestClassVisitor()
        def importDeclaration = astNodeFactory.importDeclaration(Object)

        when:
        testClassVisitor.visit(importDeclaration)

        then:
        testClassVisitor.classModelBuilder.imports == [importDeclaration]
    }
}
