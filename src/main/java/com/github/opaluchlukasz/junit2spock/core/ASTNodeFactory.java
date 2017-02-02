package com.github.opaluchlukasz.junit2spock.core;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import java.util.List;
import java.util.Map;

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
        MethodInvocation methodInvocation = ast.newMethodInvocation();
        methodInvocation.setName(simpleName(name));
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
        if (astNode instanceof SimpleType) {
            return simpleType(((SimpleType) astNode).getName().getFullyQualifiedName());
        }
        throw new UnsupportedOperationException("Unsupported astNode type:" + astNode.getClass().getName());
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
        throw new UnsupportedOperationException("Unsupported expression type:" + expression.getClass().getName());
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
