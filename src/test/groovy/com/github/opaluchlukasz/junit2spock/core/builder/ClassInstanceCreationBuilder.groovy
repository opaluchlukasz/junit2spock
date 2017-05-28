package com.github.opaluchlukasz.junit2spock.core.builder

import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration
import org.eclipse.jdt.core.dom.ClassInstanceCreation
import org.eclipse.jdt.core.dom.Type

class ClassInstanceCreationBuilder {

    private AST ast
    private Type type
    private List<ASTNode> bodyDeclarations = new LinkedList<>()
    private List<ASTNode> arguments = new LinkedList<>()

    static ClassInstanceCreationBuilder aClassInstanceCreationBuilder(AST ast) {
        new ClassInstanceCreationBuilder(ast)
    }

    private ClassInstanceCreationBuilder(AST ast) {
        this.ast = ast
    }

    ClassInstanceCreationBuilder withType(Type type) {
        this.type = type
        this
    }

    ClassInstanceCreationBuilder withBodyDeclaration(ASTNode bodyElement) {
        bodyDeclarations << bodyElement
        this
    }

    ClassInstanceCreationBuilder withArgument(ASTNode argument) {
        arguments << argument
        this
    }

    ClassInstanceCreation build() {
        ClassInstanceCreation classInstanceCreation = ast.newClassInstanceCreation()
        classInstanceCreation.setType(type)
        classInstanceCreation.arguments().addAll(arguments)
        if (!bodyDeclarations.isEmpty()) {
            AnonymousClassDeclaration anonymousClassDeclaration = ast.newAnonymousClassDeclaration()
            anonymousClassDeclaration.bodyDeclarations().addAll(bodyDeclarations)
            classInstanceCreation.setAnonymousClassDeclaration(anonymousClassDeclaration)
        }
        classInstanceCreation
    }
}
