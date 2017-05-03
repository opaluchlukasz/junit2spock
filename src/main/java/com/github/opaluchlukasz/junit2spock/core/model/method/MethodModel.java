package com.github.opaluchlukasz.junit2spock.core.model.method;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.Applicable;
import com.github.opaluchlukasz.junit2spock.core.groovism.Groovism;
import com.github.opaluchlukasz.junit2spock.core.node.IfStatementWrapper;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.groovism.GroovismChainProvider.provide;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.indent;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.indentation;
import static java.util.stream.Collectors.joining;

public abstract class MethodModel {

    static final String DEF_MODIFIER = "def ";

    private final ASTNodeFactory astNodeFactory;
    private final MethodDeclaration methodDeclaration;
    private final Groovism groovism;
    private final List<Object> body = new LinkedList<>();

    MethodModel(ASTNodeFactory astNodeFactory, MethodDeclaration methodDeclaration) {
        this.astNodeFactory = astNodeFactory;
        this.methodDeclaration = methodDeclaration;
        groovism = provide();
        if (methodDeclaration.getBody() != null && methodDeclaration.getBody().statements() != null) {
            methodDeclaration.getBody().statements().forEach(statement -> body.add(wrap(statement)));
        }
    }

    private Object wrap(Object statement) {
        if (statement instanceof IfStatement) {
            return new IfStatementWrapper((IfStatement) statement, 1, methodType());
        }
        return statement;
    }

    public String asGroovyMethod(int baseIndentationInTabs) {
        StringBuilder methodBuilder = methodDeclarationBuilder(baseIndentationInTabs);
        methodBuilder.append(" {");
        methodBuilder.append(SEPARATOR);

        methodBuilder.append(body().stream()
                .map(node -> nodeAsString(baseIndentationInTabs, node))
                .map(groovism)
                .collect(joining(SEPARATOR, "", methodSuffix())));

        indent(methodBuilder, baseIndentationInTabs).append("}");
        methodBuilder.append(SEPARATOR).append(SEPARATOR);

        return methodBuilder.toString();
    }

    private String nodeAsString(int baseIndentationInTabs, Object node) {
        if (node instanceof IfStatementWrapper) {
            return node.toString();
        }
        return indentation(baseIndentationInTabs + 1) + node.toString();
    }

    public String methodDeclaration(int baseIndentationInTabs) {
        return methodDeclarationBuilder(baseIndentationInTabs).toString();
    }

    private StringBuilder methodDeclarationBuilder(int baseIndentationInTabs) {
        StringBuilder methodBuilder = new StringBuilder();

        indent(methodBuilder, baseIndentationInTabs);
        methodBuilder.append(groovism.apply(methodModifier()));

        returnedType().ifPresent(type -> methodBuilder.append(type).append(" "));

        methodBuilder.append(getMethodName());
        methodBuilder.append(methodDeclaration.parameters().stream()
                .map(Object::toString)
                .collect(joining(", ", "(", ")")));
        return methodBuilder;
    }

    protected abstract String methodSuffix();

    protected abstract String getMethodName();

    protected abstract Applicable methodType();

    protected List<Object> body() {
        return body;
    }

    protected MethodDeclaration methodDeclaration() {
        return methodDeclaration;
    }

    protected ASTNodeFactory astNodeFactory() {
        return astNodeFactory;
    }

    private Optional<String> returnedType() {
        return Optional.ofNullable(methodDeclaration.getReturnType2())
                .filter(type -> !methodDeclaration.isConstructor())
                .filter(type -> !methodModifier().equals(DEF_MODIFIER))
                .map(Object::toString);
    }

    protected abstract String methodModifier();
}
