package com.github.opaluchlukasz.junit2spock.core.feature;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import static com.github.opaluchlukasz.junit2spock.core.model.ModifierHelper.annotatedWith;
import static java.util.Collections.singletonList;

public class MockDeclarationFeature implements Feature {

    private final ASTNodeFactory astNodeFactory;

    MockDeclarationFeature(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
    }

    @Override
    public boolean applicable(Object astNode) {
        return astNode instanceof FieldDeclaration &&
                annotatedWith(((FieldDeclaration) astNode).modifiers(), "Mock").isPresent();
    }

    @Override
    public FieldDeclaration apply(Object object) {
        FieldDeclaration fieldDeclaration = (FieldDeclaration) object;
        Annotation annotation = annotatedWith(fieldDeclaration.modifiers(), "Mock").get();
        fieldDeclaration.modifiers().remove(annotation);
        fieldDeclaration.fragments().forEach(declarationFragment ->
                ((VariableDeclarationFragment) declarationFragment).setInitializer(astNodeFactory
                        .methodInvocation("Mock", singletonList(astNodeFactory.typeLiteral(fieldDeclaration.getType().toString())))));
        return fieldDeclaration;
    }
}
