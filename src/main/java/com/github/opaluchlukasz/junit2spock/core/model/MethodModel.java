package com.github.opaluchlukasz.junit2spock.core.model;

import com.github.opaluchlukasz.junit2spock.core.util.TypeUtil;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.model.MethodDeclarationHelper.isPrivate;
import static com.github.opaluchlukasz.junit2spock.core.model.MethodDeclarationHelper.isTestMethod;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.indent;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.indentation;
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

    public String asGroovyMethod(int baseIndentationInTabs) {
        StringBuilder methodBuilder = new StringBuilder();

        indent(methodBuilder, baseIndentationInTabs).append(methodModifier());

        returnedType().ifPresent(type -> methodBuilder.append(type).append(" "));

        methodBuilder.append(methodDeclaration.getName());
        methodBuilder.append(methodDeclaration.parameters().stream()
                .map(Object::toString)
                .collect(joining(", ", "(", ") {" + SEPARATOR)));

        methodBuilder.append(body.stream()
                .map(node -> indentation(baseIndentationInTabs + 1) + node.toString())
                .collect(joining(SEPARATOR)));

        indent(methodBuilder, baseIndentationInTabs).append("}");
        methodBuilder.append(SEPARATOR).append(SEPARATOR);

        return methodBuilder.toString();
    }

    private Optional<String> returnedType() {
        return Optional.ofNullable(methodDeclaration.getReturnType2())
                .filter(type -> !TypeUtil.isVoid(type))
                .map(Object::toString);
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
