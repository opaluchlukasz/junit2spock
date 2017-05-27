package com.github.opaluchlukasz.junit2spock.core.builder

import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.Annotation
import org.eclipse.jdt.core.dom.Expression
import org.eclipse.jdt.core.dom.MethodDeclaration
import org.eclipse.jdt.core.dom.Modifier
import org.eclipse.jdt.core.dom.SingleVariableDeclaration
import org.eclipse.jdt.core.dom.Statement

class MethodDeclarationBuilder {

    private AST ast
    private String name = 'foo'
    private List<Modifier> modifiers = []
    private List<Annotation> annotations = []
    private List<SingleVariableDeclaration> parameters = []
    private List<ASTNode> body = new LinkedList<>()

    static MethodDeclarationBuilder aMethod(AST ast) {
        new MethodDeclarationBuilder(ast)
    }

    private MethodDeclarationBuilder(AST ast) {
        this.ast = ast
    }

    MethodDeclarationBuilder withName(String name) {
        this.name = name
        this
    }

    MethodDeclarationBuilder withModifier(Modifier.ModifierKeyword modifier) {
        modifiers << ast.newModifier(modifier)
        this
    }

    MethodDeclarationBuilder withParameter(SingleVariableDeclaration singleVariableDeclaration) {
        parameters << singleVariableDeclaration
        this
    }

    MethodDeclarationBuilder withAnnotation(Annotation annotation) {
        annotations << annotation
        this
    }

    MethodDeclarationBuilder withBodyExpression(Expression astNode) {
        body << ast.newExpressionStatement(astNode)
        this
    }

    MethodDeclarationBuilder withBodyStatement(Statement astNode) {
        body << astNode
        this
    }

    MethodDeclaration build() {
        MethodDeclaration method = ast.newMethodDeclaration()
        method.name = ast.newSimpleName(name)
        method.modifiers().addAll(modifiers)
        method.modifiers().addAll(annotations)
        method.parameters().addAll(parameters)
        method.body = ast.newBlock()
        method.body.statements().addAll(body)
        method
    }
}
