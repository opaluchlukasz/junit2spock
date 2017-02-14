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
import org.eclipse.jdt.core.dom.PostfixExpression
import org.eclipse.jdt.core.dom.PrefixExpression
import org.eclipse.jdt.core.dom.PrimitiveType
import org.eclipse.jdt.core.dom.SimpleName
import org.eclipse.jdt.core.dom.SimpleType
import org.eclipse.jdt.core.dom.StringLiteral
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

    @Subject
    @Shared
    private ASTNodeFactory astNodeFactory = new ASTNodeFactory()

    def 'should create import declaration'() {
        given:
        Class<Object> clazz = Object
        ImportDeclaration importDeclaration = astNodeFactory.importDeclaration(clazz)

        expect:
        importDeclaration.name.fullyQualifiedName == clazz.name
    }

    def 'should create simple name'() {
        given:
        String name = 'foo'
        SimpleName simpleName = astNodeFactory.simpleName(name)

        expect:
        simpleName.identifier == name
    }

    def 'should create simple type'() {
        given:
        String clazz = 'foo'
        SimpleType type = astNodeFactory.simpleType(clazz)

        expect:
        type.isSimpleType()
        type.name.fullyQualifiedName == clazz
        type.toString() == clazz
    }

    def 'should create primitive type'() {
        given:
        PrimitiveType type = astNodeFactory.primitiveType(CHAR)

        expect:
        type.isPrimitiveType()
        type.primitiveTypeCode == CHAR
        type.toString() == CHAR.toString()
    }

    def 'should create variable declaration with given name and default type'() {
        given:
        def someVar = 'someVar'
        VariableDeclarationStatement statement = astNodeFactory.variableDeclarationStatement(someVar)

        expect:
        statement.type instanceof PrimitiveType
        ((PrimitiveType) statement.type).primitiveTypeCode == INT
        statement.toString() == "${INT.toString()} someVar;\n" as String
    }

    def 'should create type literal'() {
        given:
        def someType = 'SomeType'
        TypeLiteral typeLiteral = astNodeFactory.typeLiteral(someType)

        expect:
        typeLiteral.type instanceof SimpleType
        ((SimpleType) typeLiteral.type).name.fullyQualifiedName == someType
    }

    def 'should create string literal'() {
        given:
        def someString = 'some string'
        StringLiteral stringLiteral = astNodeFactory.stringLiteral(someString)

        expect:
        stringLiteral.literalValue == someString
        stringLiteral.toString() == "\"$someString\""
    }

    def 'should create number literal'() {
        given:
        NumberLiteral numberLiteral = astNodeFactory.numberLiteral('5d')

        expect:
        numberLiteral.token == '5d'
        numberLiteral.toString() == '5d'
    }

    def 'should create null literal'() {
        given:
        NullLiteral nullLiteral = astNodeFactory.nullLiteral()

        expect:
        nullLiteral.toString() == 'null'
    }

    def 'should create boolean literal'() {
        given:
        BooleanLiteral booleanLiteral = astNodeFactory.booleanLiteral(false)

        expect:
        booleanLiteral.toString() == 'false'
    }

    def 'should create marker annotation'() {
        given:
        Annotation annotation = astNodeFactory.annotation('Some', [] as Map)

        expect:
        annotation.toString() == '@Some'
    }

    def 'should create annotation with parameters'() {
        given:
        Annotation annotation = astNodeFactory.annotation('Some', ['foo': astNodeFactory.stringLiteral('bar')] as Map)

        expect:
        annotation.toString() == '@Some(foo="bar")'
    }

    def 'should create infix expression'() {
        given:
        InfixExpression infixExpression = astNodeFactory
                .infixExpression(LESS_EQUALS, astNodeFactory.numberLiteral('1'), astNodeFactory.numberLiteral('2'))

        expect:
        infixExpression.toString() == '1 <= 2'
    }

    def 'should create prefix expression'() {
        given:
        PrefixExpression prefixExpression = astNodeFactory.prefixExpression(DECREMENT, astNodeFactory.simpleName('a'))

        expect:
        prefixExpression.toString() == '--a'
    }

    def 'should create postfix expression'() {
        given:
        PostfixExpression postfixExpression = astNodeFactory.postfixExpression(astNodeFactory.simpleName('a'),
                PostfixExpression.Operator.DECREMENT)

        expect:
        postfixExpression.toString() == 'a--'
    }

    def 'should create instanceof expression'() {
        given:
        InstanceofExpression instanceofExpression = astNodeFactory
                .instanceofExpression(astNodeFactory.numberLiteral('1'), astNodeFactory.simpleType('Integer'))

        expect:
        instanceofExpression.toString() == '1 instanceof Integer'
    }

    def 'should create character literal expression'() {
        when:
        CharacterLiteral characterLiteral = astNodeFactory.characterLiteral('a' as char)

        then:
        characterLiteral.toString() == "'a'"
    }

    def 'should create dimension'() {
        given:
        Dimension dimension = astNodeFactory.dimension()

        expect:
        dimension.toString() == '[]'
    }

    def 'should create array initializer'() {
        given:
        ArrayInitializer arrayInitializer = astNodeFactory.arrayInitializer([astNodeFactory.numberLiteral('13'),
                                                                             astNodeFactory.numberLiteral('14')])

        expect:
        arrayInitializer.toString() == '{13,14}'
    }

    def 'should create multidimensional array initializer'() {
        given:
        ArrayInitializer arrayInitializer1 = astNodeFactory.arrayInitializer([astNodeFactory.numberLiteral('11'),
                                                                              astNodeFactory.numberLiteral('12')])
        ArrayInitializer arrayInitializer2 = astNodeFactory.arrayInitializer([astNodeFactory.numberLiteral('13'),
                                                                              astNodeFactory.numberLiteral('14')])
        ArrayInitializer multidimensionalArrayInitializer = astNodeFactory.arrayInitializer([arrayInitializer1,
                                                                                             arrayInitializer2])

        expect:
        multidimensionalArrayInitializer.toString() == '{{11,12},{13,14}}'
    }

    def 'should create array creation expression'() {
        given:
        def arrayType = astNodeFactory.arrayType(astNodeFactory.primitiveType(INT), dimenssions.size())
        ArrayCreation arrayCreation = astNodeFactory.arrayCreation(arrayType,
                dimenssions, initializer)

        expect:
        arrayCreation.toString() == literal

        where:
        dimenssions                         | initializer                                                                                                                                | literal
        [astNodeFactory.numberLiteral('1')] | null                                                                                                                                       | 'new int[1]'
        [astNodeFactory.numberLiteral('1')] | astNodeFactory.arrayInitializer([astNodeFactory.numberLiteral('2')])                                                                       | 'new int[1]{2}'
        [astNodeFactory.numberLiteral('2'),
         astNodeFactory.numberLiteral('2')] | astNodeFactory.arrayInitializer([astNodeFactory.arrayInitializer([astNodeFactory.numberLiteral('2'), astNodeFactory.numberLiteral('3')]),
                                                                               astNodeFactory.arrayInitializer([astNodeFactory.numberLiteral('0'), astNodeFactory.numberLiteral('5')])]) | 'new int[2][2]{{2,3},{0,5}}'
    }

    def 'should create field declaration with modifiers'() {
        given:
        def variableDeclarationFragment = astNodeFactory.variableDeclarationFragment('someField')
        FieldDeclaration fieldDeclaration = astNodeFactory
                .fieldDeclaration(variableDeclarationFragment, astNodeFactory.simpleType('Comparable'), *modifiers)

        expect:
        fieldDeclaration.toString() == expectedLiteral

        where:
        modifiers                                                   | expectedLiteral
        []                                                          | 'Comparable someField;\n'
        [astNodeFactory.annotation('Immutable', emptyMap())]        | '@Immutable Comparable someField;\n'
    }
}
