package com.github.opaluchlukasz.junit2spock.core.feature

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.FieldDeclaration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class MockDeclarationFeatureTest extends Specification {
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory()

    @Subject private MockDeclarationFeature mockDeclarationFeature = new MockDeclarationFeature(nodeFactory)

    def 'should return false for non-mock declarations'() {
        expect:
        !mockDeclarationFeature.applicable(node)

        where:
        node << [new Object(), nodeFactory.fieldDeclaration(nodeFactory.variableDeclarationFragment('variable'),
                nodeFactory.simpleType('SomeClass'))]
    }

    def 'should return false for mock declarations'() {
        given:
        FieldDeclaration fieldDeclaration = nodeFactory.fieldDeclaration(nodeFactory.variableDeclarationFragment('variable'),
                nodeFactory.simpleType('SomeClass'), nodeFactory.annotation('Mock'))

        expect:
        mockDeclarationFeature.applicable(fieldDeclaration)
    }

    def 'should return Spock\'s mock for mockito mock'() {
        given:
        FieldDeclaration fieldDeclaration = nodeFactory.fieldDeclaration(nodeFactory.variableDeclarationFragment('variable'),
                nodeFactory.simpleType('SomeClass'), nodeFactory.annotation('Mock'))

        expect:
        mockDeclarationFeature.apply(fieldDeclaration).toString() == 'SomeClass variable=Mock(SomeClass.class);\n'
    }
}
