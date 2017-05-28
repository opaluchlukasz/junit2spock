package com.github.opaluchlukasz.junit2spock.core

import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.Annotation
import org.eclipse.jdt.core.dom.BooleanLiteral
import org.eclipse.jdt.core.dom.FieldDeclaration
import org.eclipse.jdt.core.dom.ImportDeclaration
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.NullLiteral
import org.eclipse.jdt.core.dom.NumberLiteral
import org.eclipse.jdt.core.dom.ParameterizedType
import org.eclipse.jdt.core.dom.PrefixExpression
import org.eclipse.jdt.core.dom.PrimitiveType
import org.eclipse.jdt.core.dom.SimpleName
import org.eclipse.jdt.core.dom.SimpleType
import org.eclipse.jdt.core.dom.SingleVariableDeclaration
import org.eclipse.jdt.core.dom.StringLiteral
import org.eclipse.jdt.core.dom.TypeLiteral
import org.eclipse.jdt.core.dom.VariableDeclarationStatement
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.LESS_EQUALS
import static org.eclipse.jdt.core.dom.PrefixExpression.Operator.DECREMENT
import static org.eclipse.jdt.core.dom.PrimitiveType.CHAR
import static org.eclipse.jdt.core.dom.PrimitiveType.INT

class ASTNodeFactoryTest extends Specification {
    private static final AST ast = newAST(JLS8)
    @Subject @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })

    def 'should create import declaration'() {
        given:
        Class<Object> clazz = Object
        ImportDeclaration importDeclaration = nodeFactory.importDeclaration(clazz)

        expect:
        importDeclaration.name.fullyQualifiedName == clazz.name
    }

    def 'should create simple name'() {
        given:
        String name = 'foo'
        SimpleName simpleName = nodeFactory.simpleName(name)

        expect:
        simpleName.identifier == name
    }

    def 'should create simple type'() {
        given:
        def clazz = 'foo'
        def type = nodeFactory.simpleType('foo')

        expect:
        type.isSimpleType()
        type.name.fullyQualifiedName == clazz
        type.toString() == clazz
    }

    def 'should create primitive type'() {
        given:
        PrimitiveType type = nodeFactory.primitiveType(CHAR)

        expect:
        type.isPrimitiveType()
        type.primitiveTypeCode == CHAR
        type.toString() == CHAR.toString()
    }

    def 'should create variable declaration with given name and default type'() {
        given:
        def someVar = 'someVar'
        VariableDeclarationStatement statement = nodeFactory.variableDeclarationStatement(someVar)

        expect:
        statement.type instanceof PrimitiveType
        ((PrimitiveType) statement.type).primitiveTypeCode == INT
        statement.toString() == "${INT.toString()} someVar;\n" as String
    }

    def 'should create type literal'() {
        given:
        def someType = nodeFactory.simpleType('SomeType')
        TypeLiteral typeLiteral = nodeFactory.typeLiteral(someType)

        expect:
        typeLiteral.type instanceof SimpleType
        ((SimpleType) typeLiteral.type).name.fullyQualifiedName == someType.toString()
    }

    def 'should create string literal'() {
        given:
        def someString = 'some string'
        StringLiteral stringLiteral = nodeFactory.stringLiteral(someString)

        expect:
        stringLiteral.literalValue == someString
        stringLiteral.toString() == "\"$someString\""
    }

    def 'should create number literal'() {
        given:
        NumberLiteral numberLiteral = nodeFactory.numberLiteral('5d')

        expect:
        numberLiteral.token == '5d'
        numberLiteral.toString() == '5d'
    }

    def 'should create null literal'() {
        given:
        NullLiteral nullLiteral = nodeFactory.nullLiteral()

        expect:
        nullLiteral.toString() == 'null'
    }

    def 'should create boolean literal'() {
        given:
        BooleanLiteral booleanLiteral = nodeFactory.booleanLiteral(false)

        expect:
        booleanLiteral.toString() == 'false'
    }

    def 'should create marker annotation'() {
        given:
        Annotation annotation = nodeFactory.annotation('Some', [] as Map)

        expect:
        annotation.toString() == '@Some'
    }

    def 'should create annotation with parameters'() {
        given:
        Annotation annotation = nodeFactory.annotation('Some', ['foo': nodeFactory.stringLiteral('bar')] as Map)

        expect:
        annotation.toString() == '@Some(foo="bar")'
    }

    def 'should create infix expression'() {
        given:
        InfixExpression infixExpression = nodeFactory
                .infixExpression(LESS_EQUALS, nodeFactory.numberLiteral('1'), nodeFactory.numberLiteral('2'))

        expect:
        infixExpression.toString() == '1 <= 2'
    }

    def 'should create prefix expression'() {
        given:
        PrefixExpression prefixExpression = nodeFactory.prefixExpression(DECREMENT, nodeFactory.simpleName('a'))

        expect:
        prefixExpression.toString() == '--a'
    }

    def 'should create field declaration with modifiers'() {
        given:
        def variableDeclarationFragment = nodeFactory.variableDeclarationFragment('someField')
        FieldDeclaration fieldDeclaration = nodeFactory
                .fieldDeclaration(variableDeclarationFragment, nodeFactory.simpleType('Comparable'), *modifiers)

        expect:
        fieldDeclaration.toString() == expectedLiteral

        where:
        modifiers                                   | expectedLiteral
        []                                          | 'Comparable someField;\n'
        [nodeFactory.markerAnnotation('Immutable')] | '@Immutable Comparable someField;\n'
    }

    def 'should create parameterized type'() {
        given:
        ParameterizedType parameterizedType = nodeFactory.parameterizedType(nodeFactory.simpleType('Map'),
                [nodeFactory.simpleType('String'), nodeFactory.simpleType('Object')])

        expect:
        parameterizedType.toString() == 'Map<String,Object>'
    }

    def 'should create single variable declaration'() {
        given:
        SingleVariableDeclaration singleVariableDeclaration = nodeFactory
                .singleVariableDeclaration(nodeFactory.simpleType('SomeClass'), 'instance')

        expect:
        singleVariableDeclaration.toString() == 'SomeClass instance'
    }
}
