package com.github.opaluchlukasz.junit2spock.core;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;

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

    public SimpleType simpleType(String name) {
        return ast.newSimpleType(simpleName(name));
    }

    public PrimitiveType primitiveType(PrimitiveType.Code code) {
        return ast.newPrimitiveType(code);
    }
}
