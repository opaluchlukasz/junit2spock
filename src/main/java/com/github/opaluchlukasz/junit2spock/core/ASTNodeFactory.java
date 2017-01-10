package com.github.opaluchlukasz.junit2spock.core;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class ASTNodeFactory {

    private final AST ast;

    public ASTNodeFactory() {
        ast = AST.newAST(AST.JLS8);
    }

    public ImportDeclaration importDeclaration(Class<?> clazz) {
        ImportDeclaration importDeclaration = ast.newImportDeclaration();
        importDeclaration.setName(ast.newName(clazz.getName()));
        return importDeclaration;
    }

    public SimpleName simpleName(String name) {
        return ast.newSimpleName(name);
    }

    public MethodInvocation methodInvocation(String name) {
        MethodInvocation methodInvocation = ast.newMethodInvocation();
        methodInvocation.setName(simpleName(name));
        return methodInvocation;
    }

    public VariableDeclarationStatement variableDeclarationStatement(String name) {
        VariableDeclarationFragment variableDeclarationFragment = ast.newVariableDeclarationFragment();
        VariableDeclarationStatement variableDeclaration = ast.newVariableDeclarationStatement(variableDeclarationFragment);
        variableDeclarationFragment.setName(simpleName(name));
        return variableDeclaration;
    }

    public SimpleType simpleType(String name) {
        return ast.newSimpleType(simpleName(name));
    }

    public PrimitiveType primitiveType(PrimitiveType.Code code) {
        return ast.newPrimitiveType(code);
    }
}
