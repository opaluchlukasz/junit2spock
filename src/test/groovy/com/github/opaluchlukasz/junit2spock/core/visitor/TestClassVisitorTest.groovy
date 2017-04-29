package com.github.opaluchlukasz.junit2spock.core.visitor

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.AstProxy
import com.github.opaluchlukasz.junit2spock.core.model.method.MethodModelFactory
import org.eclipse.jdt.core.dom.AST
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

import java.time.ZonedDateTime
import java.util.function.Supplier

import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST

@ContextConfiguration(classes = [AstProxy, ASTNodeFactory, MethodModelFactory, VisitorFactory])
class TestClassVisitorTest extends Specification {

    @Autowired private Supplier<TestClassVisitor> testClassVisitorSupplier
    private static final AST ast = newAST(JLS8)
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })

    def 'should add import to class model builder'() {
        given:
        TestClassVisitor testClassVisitor = testClassVisitorSupplier.get()
        def importDeclaration = nodeFactory.importDeclaration(ZonedDateTime)

        when:
        testClassVisitor.visit(importDeclaration)

        then:
        testClassVisitor.classModelBuilders.peek().imports == [importDeclaration]
    }

    def 'should create another class model for inner class'() {
        given:
        TestClassVisitor testClassVisitor = testClassVisitorSupplier.get()
        def typeName = 'SomeClass'
        def innerTypeName = 'SomeInnerClass'
        def typeDeclaration = nodeFactory.typeDeclaration(typeName)
        def innerTypeDeclaration = nodeFactory.typeDeclaration(innerTypeName)

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
        TestClassVisitor testClassVisitor = testClassVisitorSupplier.get()
        def typeName = 'SomeClass'
        def innerTypeName = 'SomeInnerClass'
        def typeDeclaration = nodeFactory.typeDeclaration(typeName)
        def innerTypeDeclaration = nodeFactory.typeDeclaration(innerTypeName)

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
