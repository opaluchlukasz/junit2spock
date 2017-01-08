package com.github.opaluchlukasz.junit2spock.core

import org.eclipse.jdt.core.dom.ImportDeclaration
import org.eclipse.jdt.core.dom.PrimitiveType
import org.eclipse.jdt.core.dom.SimpleName
import org.eclipse.jdt.core.dom.SimpleType
import spock.lang.Specification
import spock.lang.Subject

import static org.eclipse.jdt.core.dom.PrimitiveType.CHAR

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
}
