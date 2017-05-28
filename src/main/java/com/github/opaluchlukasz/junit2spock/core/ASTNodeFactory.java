package com.github.opaluchlukasz.junit2spock.core;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.eclipse.jdt.core.dom.PrimitiveType.INT;
import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.invokeMethod;
import static org.springframework.util.ReflectionUtils.makeAccessible;

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

    public MethodInvocation methodInvocation(String name, List<Expression> arguments) {
        return methodInvocation(name, arguments, null);
    }

    public MethodInvocation methodInvocation(String name, List<Expression> arguments, Expression expression) {
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

    public Annotation markerAnnotation(String name) {
        MarkerAnnotation annotation = ast.get().newMarkerAnnotation();
        annotation.setTypeName(simpleName(name));
        return annotation;
    }

    public ParameterizedType parameterizedType(Type type, List<Type> typeArguments) {
        ParameterizedType cloned = ast.get().newParameterizedType(type);
        cloned.typeArguments().addAll(typeArguments);
        return cloned;
    }

    public Annotation annotation(String name, Map<String, Expression> values) {
        if (values.isEmpty()) {
            return markerAnnotation(name);
        } else {
            NormalAnnotation annotation = ast.get().newNormalAnnotation();
            annotation.setTypeName(simpleName(name));
            List<MemberValuePair> valuePairs = values.entrySet().stream()
                    .map(this::memberValuePair).collect(toList());
            annotation.values().addAll(valuePairs);
            return annotation;
        }
    }

    public ExpressionStatement expressionStatement(Expression expression) {
        return ast.get().newExpressionStatement(expression);
    }

    public <T extends ASTNode> T clone(T expression) {
        if (expression == null) {
            return null;
        }
        Method method = findMethod(ASTNode.class, "clone", AST.class);
        makeAccessible(method);
        return (T) invokeMethod(method, expression, ast.get());
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

    public BooleanLiteral booleanLiteral(boolean value) {
        return ast.get().newBooleanLiteral(value);
    }

    public SimpleType simpleType(String name) {
        return ast.get().newSimpleType(simpleName(name));
    }

    public TypeLiteral typeLiteral(Type type) {
        TypeLiteral typeLiteral = ast.get().newTypeLiteral();
        typeLiteral.setType(type);
        return typeLiteral;
    }

    public NullLiteral nullLiteral() {
        return ast.get().newNullLiteral();
    }

    public Block block(Statement... statements) {
        Block block = ast.get().newBlock();
        block.statements().addAll(Arrays.asList(statements));
        return block;
    }

    public PrimitiveType primitiveType(PrimitiveType.Code code) {
        return ast.get().newPrimitiveType(code);
    }

    public ThrowStatement throwStatement(Expression toBeThrown) {
        ThrowStatement throwStatement = ast.get().newThrowStatement();
        throwStatement.setExpression(toBeThrown);
        return throwStatement;
    }

    public SingleVariableDeclaration singleVariableDeclaration(Type type, String name) {
        SingleVariableDeclaration singleVariableDeclaration = ast.get().newSingleVariableDeclaration();
        singleVariableDeclaration.setType(type);
        singleVariableDeclaration.setName(simpleName(name));
        return singleVariableDeclaration;
    }

    public ReturnStatement returnStatement(Expression expression) {
        ReturnStatement returnStatement = ast.get().newReturnStatement();
        returnStatement.setExpression(expression);
        return returnStatement;
    }

    private MemberValuePair memberValuePair(Map.Entry<String, Expression> entrySet) {
        MemberValuePair memberValuePair = ast.get().newMemberValuePair();
        memberValuePair.setName(simpleName(entrySet.getKey()));
        memberValuePair.setValue(entrySet.getValue());
        return memberValuePair;
    }
}
