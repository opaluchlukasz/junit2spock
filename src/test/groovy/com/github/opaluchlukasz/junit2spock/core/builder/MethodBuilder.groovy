package com.github.opaluchlukasz.junit2spock.core.builder

import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.Annotation
import org.eclipse.jdt.core.dom.MethodDeclaration
import org.eclipse.jdt.core.dom.Modifier

class MethodBuilder {

    private AST ast
    private String name = 'foo'
    private List<Modifier> modifiers = []
    private List<Annotation> annotations = []

    static MethodBuilder aMethod(AST ast) {
        new MethodBuilder(ast)
    }

    private MethodBuilder(AST ast) {
        this.ast = ast
    }

    MethodBuilder withName(String name) {
        this.name = name
        this
    }

    MethodBuilder withModifier(Modifier.ModifierKeyword modifier) {
        modifiers << ast.newModifier(modifier)
        this
    }

    MethodBuilder withAnnotation(Annotation annotation) {
        annotations << annotation
        this
    }

    MethodDeclaration build() {
        MethodDeclaration method = ast.newMethodDeclaration()
        method.name = ast.newSimpleName(name)
        method.modifiers().addAll(modifiers)
        method.modifiers().addAll(annotations)
        method
    }
}
