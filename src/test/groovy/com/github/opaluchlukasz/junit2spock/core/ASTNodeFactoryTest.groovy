package com.github.opaluchlukasz.junit2spock.core

import org.eclipse.jdt.core.dom.ImportDeclaration
import org.eclipse.jdt.core.dom.PrimitiveType
import org.eclipse.jdt.core.dom.SimpleName
import org.eclipse.jdt.core.dom.SimpleType
import org.eclipse.jdt.core.dom.TypeLiteral
import org.eclipse.jdt.core.dom.VariableDeclarationStatement
import spock.lang.Specification
import spock.lang.Subject

import static org.eclipse.jdt.core.dom.PrimitiveType.CHAR
import static org.eclipse.jdt.core.dom.PrimitiveType.INT

class ASTNodeFactoryTest extends Specification {

    @Subject private ASTNodeFactory astNodeFactory = new ASTNodeFactory()

    def 'should create import declaration'() {
        given:
        Class<Object> clazz = Object

        when:
        ImportDeclaration importDeclaration = astNodeFactory.importDeclaration(clazz)

        then:
        importDeclaration.name.fullyQualifiedName == clazz.name
    }

    def 'should create simple name'() {
        given:
        String name = 'foo'

        when:
        SimpleName simpleName = astNodeFactory.simpleName(name)

        then:
        simpleName.identifier == name
    }

    def 'should create simple type'() {
        given:
        String clazz = 'foo'

        when:
        SimpleType type = astNodeFactory.simpleType(clazz)

        then:
        type.isSimpleType()
        type.name.fullyQualifiedName == clazz
        type.toString() == clazz
    }

    def 'should create primitive type'() {
        when:
        PrimitiveType type = astNodeFactory.primitiveType(CHAR)

        then:
        type.isPrimitiveType()
        type.primitiveTypeCode == CHAR
        type.toString() == CHAR.toString()
    }

    def 'should create variable declaration with given name and default type'() {
        given:
        def someVar = 'someVar'

        when:
        VariableDeclarationStatement statement = astNodeFactory.variableDeclarationStatement(someVar)

        then:
        statement.type instanceof PrimitiveType
        ((PrimitiveType) statement.type).primitiveTypeCode == INT
        statement.toString() == "${INT.toString()} someVar;\n" as String
    }

    def 'should create type literal'() {
        given:
        def someType = 'SomeType'

        when:
        TypeLiteral typeLiteral = astNodeFactory.typeLiteral(someType)

        then:
        typeLiteral.type instanceof SimpleType
        ((SimpleType) typeLiteral.type).name.fullyQualifiedName == someType
    }
}
