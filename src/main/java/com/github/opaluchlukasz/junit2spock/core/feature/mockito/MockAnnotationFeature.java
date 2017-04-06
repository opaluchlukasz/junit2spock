package com.github.opaluchlukasz.junit2spock.core.feature.mockito;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.feature.Feature;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.model.ModifierHelper.annotatedWith;
import static java.util.Collections.singletonList;

public class MockAnnotationFeature extends Feature<Annotation> {

    private final ASTNodeFactory astNodeFactory;

    public MockAnnotationFeature(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
    }

    @Override
    public Optional<Annotation> applicable(Object astNode) {
        if (astNode instanceof FieldDeclaration) {
            return annotatedWith(((FieldDeclaration) astNode).modifiers(), "Mock");
        } else {
            return Optional.empty();
        }
    }

    @Override
    public FieldDeclaration apply(Object object, Annotation annotation) {
        FieldDeclaration fieldDeclaration = (FieldDeclaration) object;
        fieldDeclaration.modifiers().remove(annotation);
        fieldDeclaration.fragments().forEach(declarationFragment -> {
            Type clonedType = mockType(fieldDeclaration.getType());
            ((VariableDeclarationFragment) declarationFragment).setInitializer(astNodeFactory
                    .methodInvocation("Mock", singletonList(astNodeFactory.typeLiteral(clonedType))));
        });
        return fieldDeclaration;
    }

    private Type mockType(Type type) {
        while (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getType();
        }
        return astNodeFactory.clone(type);
    }
}
