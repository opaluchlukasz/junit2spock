package com.github.opaluchlukasz.junit2spock.core

import org.eclipse.jdt.core.dom.ImportDeclaration
import org.eclipse.jdt.core.dom.SimpleName
import spock.lang.Specification
import spock.lang.Subject

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
}
