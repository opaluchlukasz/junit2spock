package com.github.opaluchlukasz.junit2spock.core

import org.eclipse.jdt.core.dom.Annotation
import org.eclipse.jdt.core.dom.ArrayCreation
import org.eclipse.jdt.core.dom.ArrayInitializer
import org.eclipse.jdt.core.dom.BooleanLiteral
import org.eclipse.jdt.core.dom.CharacterLiteral
import org.eclipse.jdt.core.dom.Dimension
import org.eclipse.jdt.core.dom.FieldDeclaration
import org.eclipse.jdt.core.dom.ImportDeclaration
import org.eclipse.jdt.core.dom.InfixExpression
import org.eclipse.jdt.core.dom.InstanceofExpression
import org.eclipse.jdt.core.dom.NullLiteral
import org.eclipse.jdt.core.dom.NumberLiteral
import org.eclipse.jdt.core.dom.ParameterizedType
import org.eclipse.jdt.core.dom.PostfixExpression
import org.eclipse.jdt.core.dom.PrefixExpression
import org.eclipse.jdt.core.dom.PrimitiveType
import org.eclipse.jdt.core.dom.SimpleName
import org.eclipse.jdt.core.dom.SimpleType
import org.eclipse.jdt.core.dom.StringLiteral
import org.eclipse.jdt.core.dom.Type
import org.eclipse.jdt.core.dom.TypeLiteral
import org.eclipse.jdt.core.dom.VariableDeclarationStatement
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static java.util.Collections.emptyMap
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.LESS_EQUALS
import static org.eclipse.jdt.core.dom.PrefixExpression.Operator.DECREMENT
import static org.eclipse.jdt.core.dom.PrimitiveType.CHAR
import static org.eclipse.jdt.core.dom.PrimitiveType.INT

class ASTNodeFactoryTest extends Specification {

    @Subject @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory()

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
        def clazz = nodeFactory.simpleName('foo')
        def type = nodeFactory.simpleType(clazz)

        expect:
        type.isSimpleType()
        type.name.fullyQualifiedName == clazz.fullyQualifiedName
        type.toString() == clazz.fullyQualifiedName
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
        def someType = nodeFactory.simpleType(nodeFactory.simpleName('SomeType'))
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

    def 'should create postfix expression'() {
        given:
        PostfixExpression postfixExpression = nodeFactory.postfixExpression(nodeFactory.simpleName('a'),
                PostfixExpression.Operator.DECREMENT)

        expect:
        postfixExpression.toString() == 'a--'
    }

    def 'should create instanceof expression'() {
        given:
        InstanceofExpression instanceofExpression = nodeFactory
                .instanceofExpression(nodeFactory.numberLiteral('1'), nodeFactory
                .simpleType(nodeFactory.simpleName('Integer')))

        expect:
        instanceofExpression.toString() == '1 instanceof Integer'
    }

    def 'should create character literal expression'() {
        when:
        CharacterLiteral characterLiteral = nodeFactory.characterLiteral('a' as char)

        then:
        characterLiteral.toString() == "'a'"
    }

    def 'should create dimension'() {
        given:
        Dimension dimension = nodeFactory.dimension()

        expect:
        dimension.toString() == '[]'
    }

    def 'should create array initializer'() {
        given:
        ArrayInitializer arrayInitializer = nodeFactory.arrayInitializer([nodeFactory.numberLiteral('13'),
                                                                          nodeFactory.numberLiteral('14')])

        expect:
        arrayInitializer.toString() == '{13,14}'
    }

    def 'should create multidimensional array initializer'() {
        given:
        ArrayInitializer arrayInitializer1 = nodeFactory.arrayInitializer([nodeFactory.numberLiteral('11'),
                                                                           nodeFactory.numberLiteral('12')])
        ArrayInitializer arrayInitializer2 = nodeFactory.arrayInitializer([nodeFactory.numberLiteral('13'),
                                                                           nodeFactory.numberLiteral('14')])
        ArrayInitializer multidimensionalArrayInitializer = nodeFactory.arrayInitializer([arrayInitializer1,
                                                                                          arrayInitializer2])

        expect:
        multidimensionalArrayInitializer.toString() == '{{11,12},{13,14}}'
    }

    def 'should create array creation expression'() {
        given:
        def arrayType = nodeFactory.arrayType(nodeFactory.primitiveType(INT), dimenssions.size())
        ArrayCreation arrayCreation = nodeFactory.arrayCreation(arrayType,
                dimenssions, initializer)

        expect:
        arrayCreation.toString() == literal

        where:
        dimenssions                      | initializer                                                                                                                    | literal
        [nodeFactory.numberLiteral('1')] | null                                                                                                                           | 'new int[1]'
        [nodeFactory.numberLiteral('1')] | nodeFactory.arrayInitializer([nodeFactory.numberLiteral('2')])                                                                 | 'new int[1]{2}'
        [nodeFactory.numberLiteral('2'),
         nodeFactory.numberLiteral('2')] | nodeFactory.arrayInitializer([nodeFactory.arrayInitializer([nodeFactory.numberLiteral('2'), nodeFactory.numberLiteral('3')]),
                                                                         nodeFactory.arrayInitializer([nodeFactory.numberLiteral('0'), nodeFactory.numberLiteral('5')])]) | 'new int[2][2]{{2,3},{0,5}}'
    }

    def 'should create field declaration with modifiers'() {
        given:
        def variableDeclarationFragment = nodeFactory.variableDeclarationFragment('someField')
        FieldDeclaration fieldDeclaration = nodeFactory
                .fieldDeclaration(variableDeclarationFragment, nodeFactory.simpleType(nodeFactory.simpleName('Comparable')), *modifiers)

        expect:
        fieldDeclaration.toString() == expectedLiteral

        where:
        modifiers                                         | expectedLiteral
        []                                                | 'Comparable someField;\n'
        [nodeFactory.annotation('Immutable', emptyMap())] | '@Immutable Comparable someField;\n'
    }

    def 'should create parameterized type'() {
        given:
        ParameterizedType parameterizedType = nodeFactory.parameterizedType(nodeFactory.simpleType(nodeFactory.simpleName('Map')),
                [nodeFactory.simpleType(nodeFactory.simpleName('String')), nodeFactory.simpleType(nodeFactory.simpleName('Object'))])

        expect:
        parameterizedType.toString() == 'Map<String,Object>'
    }

    def 'should clone parameterized type'() {
        given:
        ParameterizedType parameterizedType = nodeFactory.parameterizedType(nodeFactory.simpleType(nodeFactory.simpleName('Map')),
                [nodeFactory.simpleType(nodeFactory.simpleName('String')), nodeFactory.simpleType(nodeFactory.simpleName('Object'))])

        when:
        Type clonedParameterizedType = nodeFactory.clone(parameterizedType)

        then:
        parameterizedType.toString() == clonedParameterizedType.toString()
    }
}
