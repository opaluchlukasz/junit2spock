package com.github.opaluchlukasz.junit2spock.core

import org.eclipse.jdt.core.dom.Annotation
import org.eclipse.jdt.core.dom.BooleanLiteral
import org.eclipse.jdt.core.dom.ImportDeclaration
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.NullLiteral
import org.eclipse.jdt.core.dom.NumberLiteral
import org.eclipse.jdt.core.dom.PrimitiveType
import org.eclipse.jdt.core.dom.SimpleName
import org.eclipse.jdt.core.dom.SimpleType
import org.eclipse.jdt.core.dom.StringLiteral
import org.eclipse.jdt.core.dom.TypeLiteral
import org.eclipse.jdt.core.dom.VariableDeclarationStatement
import spock.lang.Specification
import spock.lang.Subject

import static org.eclipse.jdt.core.dom.InfixExpression.Operator.LESS_EQUALS
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

    def 'should create string literal'() {
        given:
        def someString = 'some string'

        when:
        StringLiteral stringLiteral = astNodeFactory.stringLiteral(someString)

        then:
        stringLiteral.literalValue == someString
        stringLiteral.toString() == "\"$someString\""
    }

    def 'should create number literal'() {
        when:
        NumberLiteral numberLiteral = astNodeFactory.numberLiteral("5d")

        then:
        numberLiteral.token == "5d"
        numberLiteral.toString() == "5d"
    }

    def 'should create null literal'() {
        when:
        NullLiteral nullLiteral = astNodeFactory.nullLiteral()

        then:
        nullLiteral.toString() == 'null'
    }

    def 'should create boolean literal'() {
        when:
        BooleanLiteral booleanLiteral = astNodeFactory.booleanLiteral(false)

        then:
        booleanLiteral.toString() == 'false'
    }

    def 'should create marker annotation'() {
        when:
        Annotation annotation = astNodeFactory.annotation('Some', [] as Map)

        then:
        annotation.toString() == '@Some'
    }

    def 'should create annotation with parameters'() {
        when:
        Annotation annotation = astNodeFactory.annotation('Some', ['foo': astNodeFactory.stringLiteral('bar')] as Map)

        then:
        annotation.toString() == '@Some(foo="bar")'
    }

    def 'should create infix expression'() {
        when:
        InfixExpression infixExpression = astNodeFactory
                .infixExpression(LESS_EQUALS, astNodeFactory.numberLiteral("1"), astNodeFactory.numberLiteral("2"))

        then:
        infixExpression.toString() == '1 <= 2'
    }
}
