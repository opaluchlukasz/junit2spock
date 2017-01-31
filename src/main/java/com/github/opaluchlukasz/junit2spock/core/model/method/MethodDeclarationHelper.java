package com.github.opaluchlukasz.junit2spock.core.model.method;

import com.github.opaluchlukasz.junit2spock.core.model.ModifierHelper;
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

    static Optional<Annotation> annotatedWith(MethodDeclaration methodDeclaration, String annotationName) {
        return ModifierHelper.annotatedWith(methodDeclaration.modifiers(), annotationName);
    }
}
