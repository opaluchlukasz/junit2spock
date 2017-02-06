package com.github.opaluchlukasz.junit2spock.core;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class ASTNodeFactory {

    private final AST ast;

    public ASTNodeFactory() {
        ast = AST.newAST(AST.JLS8);
    }

    public ASTNodeFactory(AST ast) {
        this.ast = ast;
    }

    public ImportDeclaration importDeclaration(Class<?> clazz) {
        ImportDeclaration importDeclaration = ast.newImportDeclaration();
        importDeclaration.setName(ast.newName(clazz.getName()));
        return importDeclaration;
    }

    public SimpleName simpleName(String name) {
        return ast.newSimpleName(name);
    }

    public MethodInvocation methodInvocation(String name, List<ASTNode> arguments) {
        return methodInvocation(name, arguments, null);
    }

    public MethodInvocation methodInvocation(String name, List<ASTNode> arguments, Expression expression) {
        MethodInvocation methodInvocation = ast.newMethodInvocation();
        methodInvocation.setName(simpleName(name));
        Optional.ofNullable(expression).ifPresent(ex -> methodInvocation.setExpression(ex));
        arguments.forEach(astNode -> methodInvocation.arguments().add(astNode));
        return methodInvocation;
    }

    public NumberLiteral numberLiteral(String token) {
        NumberLiteral numberLiteral = ast.newNumberLiteral();
        numberLiteral.setToken(token);
        return numberLiteral;
    }

    public StringLiteral stringLiteral(String token) {
        StringLiteral stringLiteral = ast.newStringLiteral();
        stringLiteral.setLiteralValue(token);
        return stringLiteral;
    }

    public VariableDeclarationStatement variableDeclarationStatement(String name) {
        VariableDeclarationFragment variableDeclarationFragment = ast.newVariableDeclarationFragment();
        VariableDeclarationStatement variableDeclaration = ast.newVariableDeclarationStatement(variableDeclarationFragment);
        variableDeclarationFragment.setName(simpleName(name));
        return variableDeclaration;
    }

    public InfixExpression infixExpression(InfixExpression.Operator operator, Expression leftOperand, Expression rightOperand) {
        InfixExpression infixExpression = ast.newInfixExpression();
        infixExpression.setOperator(operator);
        infixExpression.setLeftOperand(leftOperand);
        infixExpression.setRightOperand(rightOperand);
        return infixExpression;
    }

    private Annotation annotation(String name) {
        MarkerAnnotation annotation = ast.newMarkerAnnotation();
        annotation.setTypeName(simpleName(name));
        return annotation;
    }

    public Annotation annotation(String name, Map<String, Expression> values) {
        if (values.isEmpty()) {
            return annotation(name);
        } else {
            NormalAnnotation annotation = ast.newNormalAnnotation();
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
        return ast.newDimension();
    }

    public Type clone(Type type) {
        if (type instanceof SimpleType) {
            return simpleType(((SimpleType) type).getName().getFullyQualifiedName());
        }
        if (type instanceof PrimitiveType) {
            return primitiveType(((PrimitiveType) type).getPrimitiveTypeCode());
        }
        if (type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) type;
            return arrayType(clone(arrayType.getElementType()), arrayType.dimensions().size());
        }
        throw new UnsupportedOperationException("Unsupported astNode type:" + type.getClass().getName());
    }

    public ArrayType arrayType(Type type, int dimensions) {
        return ast.newArrayType(type, dimensions);
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
        if (expression instanceof ThisExpression) {
            return ast.newThisExpression();
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

    public ArrayInitializer arrayInitializer(List expressions) {
        ArrayInitializer clonedArrayInitializer = ast.newArrayInitializer();
        clonedArrayInitializer.expressions().addAll(expressions);
        return clonedArrayInitializer;
    }

    public ArrayCreation arrayCreation(ArrayType arrayType, List dimensions, ArrayInitializer arrayInitializer) {
        ArrayCreation clonedArrayCreation = ast.newArrayCreation();
        clonedArrayCreation.setType(arrayType);
        clonedArrayCreation.setInitializer(arrayInitializer);
        clonedArrayCreation.dimensions().addAll(dimensions);
        return clonedArrayCreation;
    }

    public PostfixExpression postfixExpression(Expression expression, PostfixExpression.Operator operator) {
        PostfixExpression postfixExpression = ast.newPostfixExpression();
        postfixExpression.setOperator(operator);
        postfixExpression.setOperand(expression);
        return postfixExpression;
    }

    public PrefixExpression prefixExpression(PrefixExpression.Operator operator, Expression expression) {
        PrefixExpression prefixExpression = ast.newPrefixExpression();
        prefixExpression.setOperator(operator);
        prefixExpression.setOperand(expression);
        return prefixExpression;
    }

    public InstanceofExpression instanceofExpression(Expression expression, Type type) {
        InstanceofExpression instanceofExpression = ast.newInstanceofExpression();
        instanceofExpression.setLeftOperand(expression);
        instanceofExpression.setRightOperand(type);
        return instanceofExpression;
    }

    public CharacterLiteral characterLiteral(char c) {
        CharacterLiteral characterLiteral = ast.newCharacterLiteral();
        characterLiteral.setCharValue(c);
        return characterLiteral;
    }

    public BooleanLiteral booleanLiteral(boolean value) {
        return ast.newBooleanLiteral(value);
    }

    public SimpleType simpleType(String name) {
        return ast.newSimpleType(simpleName(name));
    }

    public TypeLiteral typeLiteral(String typeName) {
        TypeLiteral typeLiteral = ast.newTypeLiteral();
        typeLiteral.setType(simpleType(typeName));
        return typeLiteral;
    }

    public NullLiteral nullLiteral() {
        return ast.newNullLiteral();
    }

    public PrimitiveType primitiveType(PrimitiveType.Code code) {
        return ast.newPrimitiveType(code);
    }

    private MethodInvocation methodInvocation(MethodInvocation methodInvocation) {
        MethodInvocation clonedMethodInvocation = ast.newMethodInvocation();
        clonedMethodInvocation.setName(ast.newSimpleName(methodInvocation.getName().toString()));

        List cloned = (List) methodInvocation.arguments().stream().map(this::clone).collect(toList());
        clonedMethodInvocation.arguments().addAll(cloned);
        clonedMethodInvocation.setExpression(clone(methodInvocation.getExpression()));
        return clonedMethodInvocation;
    }

    private MemberValuePair memberValuePair(Map.Entry<String, Expression> entrySet) {
        MemberValuePair memberValuePair = ast.newMemberValuePair();
        memberValuePair.setName(simpleName(entrySet.getKey()));
        memberValuePair.setValue(entrySet.getValue());
        return memberValuePair;
    }

    private Expression fieldAccess(FieldAccess toBeCopied) {
        FieldAccess fieldAccess = ast.newFieldAccess();
        fieldAccess.setExpression(clone(toBeCopied.getExpression()));
        fieldAccess.setName((SimpleName) clone(toBeCopied.getName()));
        return fieldAccess;
    }

    private Expression classInstanceCreation(ClassInstanceCreation toBeCloned) {
        ClassInstanceCreation clonedClassInstanceCreation = ast.newClassInstanceCreation();
        clonedClassInstanceCreation.setExpression(clone(toBeCloned.getExpression()));
        clonedClassInstanceCreation.setType(simpleType(toBeCloned.getType().toString()));
        List cloned = (List) toBeCloned.arguments().stream().map(this::clone).collect(toList());
        clonedClassInstanceCreation.arguments().addAll(cloned);
        return clonedClassInstanceCreation;
    }
}
