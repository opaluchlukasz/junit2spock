package com.github.opaluchlukasz.junit2spock.core.feature.mockito

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.FieldDeclaration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class MockAnnotationFeatureTest extends Specification {

    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory()

    @Subject private MockAnnotationFeature mockDeclarationFeature = new MockAnnotationFeature(nodeFactory)

    def 'should return false for non-mock declarations'() {
        expect:
        !mockDeclarationFeature.applicable(node).isPresent()

        where:
        node << [new Object(), nodeFactory.fieldDeclaration(nodeFactory.variableDeclarationFragment('variable'),
                nodeFactory.simpleType(nodeFactory.simpleName('SomeClass')))]
    }

    def 'should return false for mock declarations'() {
        given:
        FieldDeclaration fieldDeclaration = nodeFactory.fieldDeclaration(nodeFactory.variableDeclarationFragment('variable'),
                nodeFactory.simpleType(nodeFactory.simpleName('SomeClass')), nodeFactory.annotation('Mock'))

        expect:
        mockDeclarationFeature.applicable(fieldDeclaration).isPresent()
    }

    def 'should return Spock\'s mock for mockito mock'() {
        given:
        FieldDeclaration fieldDeclaration = nodeFactory.fieldDeclaration(nodeFactory.variableDeclarationFragment('variable'),
                nodeFactory.simpleType(nodeFactory.simpleName('SomeClass')), nodeFactory.annotation('Mock'))

        expect:
        mockDeclarationFeature.apply(fieldDeclaration).toString() == 'SomeClass variable=Mock(SomeClass.class);\n'
    }
}
