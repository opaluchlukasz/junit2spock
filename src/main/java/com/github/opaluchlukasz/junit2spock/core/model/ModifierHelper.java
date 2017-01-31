package com.github.opaluchlukasz.junit2spock.core.model;

import org.eclipse.jdt.core.dom.Annotation;

import java.util.List;
import java.util.Optional;

public final class ModifierHelper {

    private ModifierHelper() {
        //NOOP
    }

    public static Optional<Annotation> annotatedWith(List modifiers, String annotationName) {
        Optional<?> optionalAnnotation = modifiers.stream()
                .filter(modifier -> modifier instanceof Annotation)
                .filter(modifier -> ((Annotation) modifier).getTypeName().getFullyQualifiedName().equals(annotationName)).findFirst();
        return optionalAnnotation.map(annotation -> (Annotation) annotation);
    }
}
