package com.github.opaluchlukasz.junit2spock.core.model;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.Util.SEPARATOR;
import static com.github.opaluchlukasz.junit2spock.core.model.MethodDeclarationHelper.isPrivate;
import static com.github.opaluchlukasz.junit2spock.core.model.MethodDeclarationHelper.isTestMethod;
import static java.util.stream.Collectors.joining;

public class MethodModel {

    private final MethodDeclaration methodDeclaration;
    private final List<ASTNode> body = new LinkedList<>();

    public MethodModel(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
        if (methodDeclaration.getBody() != null && methodDeclaration.getBody().statements() != null) {
            this.body.addAll(methodDeclaration.getBody().statements());
        }
    }

    public String asGroovyMethod() {
        StringBuilder methodBuilder = new StringBuilder();

        methodBuilder.append(methodModifier());

        returnedType().ifPresent(type -> methodBuilder.append(type).append(" "));

        methodBuilder.append(methodDeclaration.getName());
        methodBuilder.append(methodDeclaration.parameters().stream()
                .map(Object::toString)
                .collect(joining(", ", "(", ") {" + SEPARATOR)));

        methodBuilder.append(body.stream()
                .map(Object::toString)
                .collect(joining(SEPARATOR, "\t", "")));

        methodBuilder.append("}");
        methodBuilder.append(SEPARATOR).append(SEPARATOR);

        return methodBuilder.toString();
    }

    private Optional<String> returnedType() {
        return Optional.ofNullable(methodDeclaration.getReturnType2()).map(Object::toString);
    }

    private String methodModifier() {
        if (isPrivate(methodDeclaration)) {
            return "private ";
        } else if (isTestMethod(methodDeclaration)) {
            return "def ";
        }
        return "";
    }
}
