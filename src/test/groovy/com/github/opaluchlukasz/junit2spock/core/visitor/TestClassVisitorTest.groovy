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
        testClassVisitor.classModelBuilders.peek().imports == [importDeclaration]
    }

    def 'should create another class model for inner class'() {
        given:
        TestClassVisitor testClassVisitor = new TestClassVisitor()
        def typeName = 'SomeClass'
        def innerTypeName = 'SomeInnerClass'
        def typeDeclaration = astNodeFactory.typeDeclaration(typeName)
        def innerTypeDeclaration = astNodeFactory.typeDeclaration(innerTypeName)

        when:
        testClassVisitor.visit(typeDeclaration)

        then:
        testClassVisitor.classModelBuilders.peek().className() == typeName

        when:
        testClassVisitor.visit(innerTypeDeclaration)

        then:
        testClassVisitor.classModelBuilders.size() == 2
        testClassVisitor.classModelBuilders.peek().className() == innerTypeName
    }

    def 'should persist inner type model within parent class'() {
        given:
        TestClassVisitor testClassVisitor = new TestClassVisitor()
        def typeName = 'SomeClass'
        def innerTypeName = 'SomeInnerClass'
        def typeDeclaration = astNodeFactory.typeDeclaration(typeName)
        def innerTypeDeclaration = astNodeFactory.typeDeclaration(innerTypeName)

        when:
        testClassVisitor.visit(typeDeclaration)
        testClassVisitor.visit(innerTypeDeclaration)
        testClassVisitor.endVisit(innerTypeDeclaration)

        then:
        testClassVisitor.classModelBuilders.size() == 1
        testClassVisitor.classModelBuilders.peek().className == typeName
        testClassVisitor.classModelBuilders.peek().innerTypes.size() == 1
        testClassVisitor.classModelBuilders.peek().innerTypes[0].typeName() == innerTypeName
    }
}
