package com.github.opaluchlukasz.junit2spock.core.model;

import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;

public final class MethodDeclarationHelper {

    private MethodDeclarationHelper() {
        //NOOP
    }

    public static boolean isPrivate(MethodDeclaration methodDeclaration) {
        return Modifier.isPrivate(methodDeclaration.getModifiers());
    }

    public static boolean isTestMethod(MethodDeclaration methodDeclaration) {
        return methodDeclaration.modifiers().stream().anyMatch(modifier -> {
            if (modifier instanceof MarkerAnnotation) {
                MarkerAnnotation annotation = (MarkerAnnotation) modifier;
                return annotation.getTypeName().getFullyQualifiedName().equals("Test");
            }
            return false;
        });
    }
}
