package com.github.opaluchlukasz.junit2spock.core.visitor

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.util.TestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import java.time.ZonedDateTime
import java.util.function.Supplier

@ContextConfiguration(classes = TestConfig)
class TypeVisitorTest extends Specification {

    @Autowired private Supplier<TypeVisitor> typeVisitorSupplier
    @Autowired private ASTNodeFactory nodeFactory

    def 'should add import to type model builder'() {
        given:
        TypeVisitor typeVisitor = typeVisitorSupplier.get()
        def importDeclaration = nodeFactory.importDeclaration(ZonedDateTime)

        when:
        typeVisitor.visit(importDeclaration)

        then:
        typeVisitor.typeModelBuilders.peek().imports == [importDeclaration]
    }

    def 'should create another type model for inner type'() {
        given:
        TypeVisitor typeVisitor = typeVisitorSupplier.get()
        def typeName = 'SomeClass'
        def innerTypeName = 'SomeInnerClass'
        def typeDeclaration = nodeFactory.typeDeclaration(typeName)
        def innerTypeDeclaration = nodeFactory.typeDeclaration(innerTypeName)

        when:
        typeVisitor.visit(typeDeclaration)

        then:
        typeVisitor.typeModelBuilders.peek().typeName() == typeName

        when:
        typeVisitor.visit(innerTypeDeclaration)

        then:
        typeVisitor.typeModelBuilders.size() == 2
        typeVisitor.typeModelBuilders.peek().typeName() == innerTypeName
    }

    def 'should persist inner type model within parent type'() {
        given:
        TypeVisitor typeVisitor = typeVisitorSupplier.get()
        def typeName = 'SomeClass'
        def innerTypeName = 'SomeInnerClass'
        def typeDeclaration = nodeFactory.typeDeclaration(typeName)
        def innerTypeDeclaration = nodeFactory.typeDeclaration(innerTypeName)

        when:
        typeVisitor.visit(typeDeclaration)
        typeVisitor.visit(innerTypeDeclaration)
        typeVisitor.endVisit(innerTypeDeclaration)

        then:
        typeVisitor.typeModelBuilders.size() == 1
        typeVisitor.typeModelBuilders.peek().typeName == typeName
        typeVisitor.typeModelBuilders.peek().innerTypes.size() == 1
        typeVisitor.typeModelBuilders.peek().innerTypes[0].typeName() == innerTypeName
    }
}
