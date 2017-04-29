package com.github.opaluchlukasz.junit2spock.core;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.eclipse.jdt.core.dom.PrimitiveType.INT;

@Component
public class ASTNodeFactory {

    private final AstProvider ast;

    @Autowired
    public ASTNodeFactory(AstProvider astProvider) {
        this.ast = astProvider;
    }

    public ImportDeclaration importDeclaration(Class<?> clazz) {
        return importDeclaration(clazz.getName(), false);
    }

    public ImportDeclaration importDeclaration(String name, boolean isStatic) {
        ImportDeclaration importDeclaration = ast.get().newImportDeclaration();
        importDeclaration.setName(ast.get().newName(name));
        importDeclaration.setStatic(isStatic);
        return importDeclaration;
    }

    public SimpleName simpleName(String name) {
        return ast.get().newSimpleName(name);
    }

    public PackageDeclaration packageDeclaration(Name name) {
        PackageDeclaration packageDeclaration = ast.get().newPackageDeclaration();
        packageDeclaration.setName(name);
        return packageDeclaration;
    }

    public MethodInvocation methodInvocation(String name, List<ASTNode> arguments) {
        return methodInvocation(name, arguments, null);
    }

    public MethodInvocation methodInvocation(String name, List<ASTNode> arguments, Expression expression) {
        MethodInvocation methodInvocation = ast.get().newMethodInvocation();
        methodInvocation.setName(simpleName(name));
        Optional.ofNullable(expression).ifPresent(methodInvocation::setExpression);
        arguments.forEach(astNode -> methodInvocation.arguments().add(astNode));
        return methodInvocation;
    }

    public FieldAccess fieldAccess(String name, Expression expression) {
        FieldAccess fieldAccess = ast.get().newFieldAccess();
        fieldAccess.setName(simpleName(name));
        Optional.ofNullable(expression).ifPresent(fieldAccess::setExpression);
        return fieldAccess;
    }

    public NumberLiteral numberLiteral(String token) {
        NumberLiteral numberLiteral = ast.get().newNumberLiteral();
        numberLiteral.setToken(token);
        return numberLiteral;
    }

    public ParenthesizedExpression parenthesizedExpression(Expression expression) {
        ParenthesizedExpression parenthesizedExpression = ast.get().newParenthesizedExpression();
        parenthesizedExpression.setExpression(expression);
        return parenthesizedExpression;
    }

    public StringLiteral stringLiteral(String token) {
        StringLiteral stringLiteral = ast.get().newStringLiteral();
        stringLiteral.setLiteralValue(token);
        return stringLiteral;
    }

    public VariableDeclarationStatement variableDeclarationStatement(String name) {
        return variableDeclarationStatement(name, primitiveType(INT), null);
    }

    public ClassInstanceCreation classInstanceCreation(Type type, ASTNode... arguments) {
        ClassInstanceCreation classInstanceCreation = ast.get().newClassInstanceCreation();
        classInstanceCreation.setType(type);
        classInstanceCreation.arguments().addAll(asList(arguments));
        return classInstanceCreation;
    }

    public VariableDeclarationStatement variableDeclarationStatement(String name, Type type, Expression initializer) {
        VariableDeclarationFragment variableDeclarationFragment = variableDeclarationFragment(name);
        variableDeclarationFragment.setInitializer(initializer);
        VariableDeclarationStatement statement = ast.get().newVariableDeclarationStatement(variableDeclarationFragment);
        statement.setType(type);
        return statement;
    }

    public VariableDeclarationFragment variableDeclarationFragment(String name) {
        VariableDeclarationFragment variableDeclarationFragment = ast.get().newVariableDeclarationFragment();
        variableDeclarationFragment.setName(simpleName(name));
        return variableDeclarationFragment;
    }

    public InfixExpression infixExpression(InfixExpression.Operator operator, Expression leftOperand, Expression rightOperand) {
        InfixExpression infixExpression = ast.get().newInfixExpression();
        infixExpression.setOperator(operator);
        infixExpression.setLeftOperand(leftOperand);
        infixExpression.setRightOperand(rightOperand);
        return infixExpression;
    }

    private Annotation annotation(String name) {
        MarkerAnnotation annotation = ast.get().newMarkerAnnotation();
        annotation.setTypeName(simpleName(name));
        return annotation;
    }

    public Annotation annotation(String name, Map<String, Expression> values) {
        if (values.isEmpty()) {
            return annotation(name);
        } else {
            NormalAnnotation annotation = ast.get().newNormalAnnotation();
            annotation.setTypeName(simpleName(name));
            List<MemberValuePair> valuePairs = values.entrySet().stream()
                    .map(this::memberValuePair).collect(toList());
            annotation.values().addAll(valuePairs);
            return annotation;
        }
    }

    public ASTNode clone(Object astNode) {
        if (astNode instanceof Expression) {
            return clone(((Expression) astNode));
        }
        if (astNode instanceof Type) {
            return clone(((Type) astNode));
        }
        if (astNode instanceof Dimension) {
            return dimension();
        }
        throw new UnsupportedOperationException("Unsupported astNode type:" + astNode.getClass().getName());
    }

    public Dimension dimension() {
        return ast.get().newDimension();
    }

    public ExpressionStatement expressionStatement(Expression expression) {
        return ast.get().newExpressionStatement(expression);
    }

    public Type clone(Type type) {
        if (type instanceof SimpleType) {
            return simpleType((Name) clone(((SimpleType) type).getName()));
        }
        if (type instanceof PrimitiveType) {
            return primitiveType(((PrimitiveType) type).getPrimitiveTypeCode());
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            List typeArguments = (List) parameterizedType.typeArguments().stream().map(typeArgument -> clone(typeArgument)).collect(toList());
            return parameterizedType(clone(parameterizedType.getType()), typeArguments);
        }
        if (type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) type;
            return arrayType(clone(arrayType.getElementType()), arrayType.dimensions().size());
        }
        throw new UnsupportedOperationException("Unsupported astNode type:" + type.getClass().getName());
    }

    public ParameterizedType parameterizedType(Type type, List typeArguments) {
        ParameterizedType cloned = ast.get().newParameterizedType(type);
        cloned.typeArguments().addAll(typeArguments);
        return cloned;
    }

    public ArrayType arrayType(Type type, int dimensions) {
        return ast.get().newArrayType(type, dimensions);
    }

    public Expression clone(Expression expression) {
        if (expression == null) {
            return null;
        }
        if (expression instanceof NumberLiteral) {
            return numberLiteral(((NumberLiteral) expression).getToken());
        }
        if (expression instanceof NullLiteral) {
            return nullLiteral();
        }
        if (expression instanceof StringLiteral) {
            return stringLiteral(((StringLiteral) expression).getLiteralValue());
        }
        if (expression instanceof BooleanLiteral) {
            return booleanLiteral(((BooleanLiteral) expression).booleanValue());
        }
        if (expression instanceof CharacterLiteral) {
            return characterLiteral(((CharacterLiteral) expression).charValue());
        }
        if (expression instanceof CastExpression) {
            CastExpression toBeCloned = (CastExpression) expression;
            return castExpression(clone(toBeCloned.getType()), clone(toBeCloned.getExpression()));
        }
        if (expression instanceof ArrayInitializer) {
            ArrayInitializer arrayInitializer = (ArrayInitializer) expression;
            List expressions = (List) arrayInitializer.expressions().stream().map(this::clone).collect(toList());
            return arrayInitializer(expressions);
        }
        if (expression instanceof InstanceofExpression) {
            InstanceofExpression instanceofExpression = (InstanceofExpression) expression;
            return instanceofExpression(clone(instanceofExpression.getLeftOperand()),
                    clone(instanceofExpression.getRightOperand()));
        }
        if (expression instanceof PrefixExpression) {
            PrefixExpression prefixExpression = (PrefixExpression) expression;
            return prefixExpression(prefixExpression.getOperator(),
                    clone(prefixExpression.getOperand()));
        }
        if (expression instanceof ParenthesizedExpression) {
            ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression;
            return parenthesizedExpression(clone(parenthesizedExpression.getExpression()));
        }
        if (expression instanceof PostfixExpression) {
            PostfixExpression postfixExpression = (PostfixExpression) expression;
            return postfixExpression(clone(postfixExpression.getOperand()),
                    postfixExpression.getOperator());
        }
        if (expression instanceof InfixExpression) {
            InfixExpression infixExpression = (InfixExpression) expression;
            return infixExpression(infixExpression.getOperator(),
                    clone(infixExpression.getLeftOperand()),
                    clone(infixExpression.getRightOperand()));
        }
        if (expression instanceof MethodInvocation) {
            return methodInvocation((MethodInvocation) expression);
        }
        if (expression instanceof SimpleName) {
            return simpleName(((SimpleName) expression).getFullyQualifiedName());
        }
        if (expression instanceof QualifiedName) {
            QualifiedName qualifiedName = (QualifiedName) expression;
            return ast.get().newQualifiedName((Name) clone(qualifiedName.getQualifier()),
                    (SimpleName) clone(qualifiedName.getName()));
        }
        if (expression instanceof TypeLiteral) {
            TypeLiteral typeLiteral = ast.get().newTypeLiteral();
            typeLiteral.setType(clone(((TypeLiteral) expression).getType()));
            return typeLiteral;
        }
        if (expression instanceof ThisExpression) {
            return ast.get().newThisExpression();
        }
        if (expression instanceof FieldAccess) {
            return fieldAccess((FieldAccess) expression);
        }
        if (expression instanceof ClassInstanceCreation) {
            return classInstanceCreation((ClassInstanceCreation) expression);
        }
        if (expression instanceof ArrayCreation) {
            ArrayCreation arrayCreation = (ArrayCreation) expression;
            List dimensions = (List) arrayCreation.dimensions().stream().map(this::clone).collect(toList());
            return arrayCreation((ArrayType) clone(arrayCreation.getType()),
                    dimensions,
                    (ArrayInitializer) clone(arrayCreation.getInitializer()));
        }
        throw new UnsupportedOperationException("Unsupported expression type:" + expression.getClass().getName());
    }

    public CastExpression castExpression(Type type, Expression expression) {
        CastExpression castExpression = ast.get().newCastExpression();
        castExpression.setExpression(expression);
        castExpression.setType(type);
        return castExpression;
    }

    public ArrayInitializer arrayInitializer(List expressions) {
        ArrayInitializer arrayInitializer = ast.get().newArrayInitializer();
        arrayInitializer.expressions().addAll(expressions);
        return arrayInitializer;
    }

    public ArrayCreation arrayCreation(ArrayType arrayType, List dimensions, ArrayInitializer arrayInitializer) {
        ArrayCreation clonedArrayCreation = ast.get().newArrayCreation();
        clonedArrayCreation.setType(arrayType);
        clonedArrayCreation.setInitializer(arrayInitializer);
        clonedArrayCreation.dimensions().addAll(dimensions);
        return clonedArrayCreation;
    }

    public PostfixExpression postfixExpression(Expression expression, PostfixExpression.Operator operator) {
        PostfixExpression postfixExpression = ast.get().newPostfixExpression();
        postfixExpression.setOperator(operator);
        postfixExpression.setOperand(expression);
        return postfixExpression;
    }

    public FieldDeclaration fieldDeclaration(VariableDeclarationFragment variableDeclarationFragment,
                                             Type type,
                                             ASTNode... modifiers) {
        FieldDeclaration fieldDeclaration = ast.get().newFieldDeclaration(variableDeclarationFragment);
        fieldDeclaration.setType(type);
        stream(modifiers).forEach(modifier -> fieldDeclaration.modifiers().add(modifier));
        return fieldDeclaration;
    }

    public PrefixExpression prefixExpression(PrefixExpression.Operator operator, Expression expression) {
        PrefixExpression prefixExpression = ast.get().newPrefixExpression();
        prefixExpression.setOperator(operator);
        prefixExpression.setOperand(expression);
        return prefixExpression;
    }

    public TypeDeclaration typeDeclaration(String name) {
        TypeDeclaration typeDeclaration = ast.get().newTypeDeclaration();
        typeDeclaration.setName(simpleName(name));
        return typeDeclaration;
    }

    public InstanceofExpression instanceofExpression(Expression expression, Type type) {
        InstanceofExpression instanceofExpression = ast.get().newInstanceofExpression();
        instanceofExpression.setLeftOperand(expression);
        instanceofExpression.setRightOperand(type);
        return instanceofExpression;
    }

    public CharacterLiteral characterLiteral(char c) {
        CharacterLiteral characterLiteral = ast.get().newCharacterLiteral();
        characterLiteral.setCharValue(c);
        return characterLiteral;
    }

    public BooleanLiteral booleanLiteral(boolean value) {
        return ast.get().newBooleanLiteral(value);
    }

    public SimpleType simpleType(Name name) {
        return ast.get().newSimpleType(name);
    }

    public TypeLiteral typeLiteral(Type type) {
        TypeLiteral typeLiteral = ast.get().newTypeLiteral();
        typeLiteral.setType(type);
        return typeLiteral;
    }

    public NullLiteral nullLiteral() {
        return ast.get().newNullLiteral();
    }

    public Block block() {
        return ast.get().newBlock();
    }

    public PrimitiveType primitiveType(PrimitiveType.Code code) {
        return ast.get().newPrimitiveType(code);
    }

    public ThrowStatement throwStatement(Expression toBeThrown) {
        ThrowStatement throwStatement = ast.get().newThrowStatement();
        throwStatement.setExpression(toBeThrown);
        return throwStatement;
    }

    private MethodInvocation methodInvocation(MethodInvocation methodInvocation) {
        MethodInvocation clonedMethodInvocation = ast.get().newMethodInvocation();
        clonedMethodInvocation.setName(ast.get().newSimpleName(methodInvocation.getName().toString()));

        List cloned = (List) methodInvocation.arguments().stream().map(this::clone).collect(toList());
        clonedMethodInvocation.arguments().addAll(cloned);
        clonedMethodInvocation.setExpression(clone(methodInvocation.getExpression()));
        return clonedMethodInvocation;
    }

    private MemberValuePair memberValuePair(Map.Entry<String, Expression> entrySet) {
        MemberValuePair memberValuePair = ast.get().newMemberValuePair();
        memberValuePair.setName(simpleName(entrySet.getKey()));
        memberValuePair.setValue(entrySet.getValue());
        return memberValuePair;
    }

    private Expression fieldAccess(FieldAccess toBeCopied) {
        FieldAccess fieldAccess = ast.get().newFieldAccess();
        fieldAccess.setExpression(clone(toBeCopied.getExpression()));
        fieldAccess.setName((SimpleName) clone(toBeCopied.getName()));
        return fieldAccess;
    }

    private Expression classInstanceCreation(ClassInstanceCreation toBeCloned) {
        ClassInstanceCreation clonedClassInstanceCreation = ast.get().newClassInstanceCreation();
        clonedClassInstanceCreation.setExpression(clone(toBeCloned.getExpression()));
        clonedClassInstanceCreation.setType(clone(toBeCloned.getType()));
        List cloned = (List) toBeCloned.arguments().stream().map(this::clone).collect(toList());
        clonedClassInstanceCreation.arguments().addAll(cloned);
        return clonedClassInstanceCreation;
    }

    public IfStatement ifStatement() {
        return ast.get().newIfStatement();
    }
}
