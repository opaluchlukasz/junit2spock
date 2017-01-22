package com.github.opaluchlukasz.junit2spock.core.model.method;

import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;

import java.util.Optional;

public final class MethodDeclarationHelper {

    private MethodDeclarationHelper() {
        //NOOP
    }

    public static boolean isPrivate(MethodDeclaration methodDeclaration) {
        return Modifier.isPrivate(methodDeclaration.getModifiers());
    }

    public static boolean isTestMethod(MethodDeclaration methodDeclaration) {
        return annotatedWith(methodDeclaration, "Test").isPresent();
    }

    public static Optional<Annotation> annotatedWith(MethodDeclaration methodDeclaration, String annotationName) {
        Optional<?> optionalAnnotation = methodDeclaration.modifiers().stream()
                .filter(modifier -> modifier instanceof Annotation)
                .filter(modifier -> ((Annotation) modifier).getTypeName().getFullyQualifiedName().equals(annotationName)).findFirst();
        return optionalAnnotation.map(annotation -> (Annotation) annotation);
    }
}
