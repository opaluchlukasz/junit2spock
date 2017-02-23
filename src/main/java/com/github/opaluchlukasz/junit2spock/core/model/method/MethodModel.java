package com.github.opaluchlukasz.junit2spock.core.model.method;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.Applicable;
import com.github.opaluchlukasz.junit2spock.core.feature.Feature;
import com.github.opaluchlukasz.junit2spock.core.feature.FeatureProvider;
import com.github.opaluchlukasz.junit2spock.core.groovism.Groovism;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.groovism.GroovismChainProvider.provide;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.indent;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.indentation;
import static com.github.opaluchlukasz.junit2spock.core.util.TypeUtil.isVoid;
import static java.util.stream.Collectors.joining;

public abstract class MethodModel {

    private final MethodDeclaration methodDeclaration;
    private final Groovism groovism;
    private final ASTNodeFactory astNodeFactory;
    private final List<Object> body = new LinkedList<>();

    MethodModel(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
        astNodeFactory = new ASTNodeFactory(methodDeclaration.getAST());
        groovism = provide();
        if (methodDeclaration.getBody() != null && methodDeclaration.getBody().statements() != null) {
            this.body.addAll(methodDeclaration.getBody().statements());
        }
    }

    public String asGroovyMethod(int baseIndentationInTabs) {
        StringBuilder methodBuilder = methodDeclarationBuilder(baseIndentationInTabs);
        methodBuilder.append(" {");
        methodBuilder.append(SEPARATOR);

        methodBuilder.append(body().stream()
                .map(node -> indentation(baseIndentationInTabs + 1) + node.toString())
                .map(groovism::apply)
                .collect(joining(SEPARATOR, "", methodSuffix())));

        indent(methodBuilder, baseIndentationInTabs).append("}");
        methodBuilder.append(SEPARATOR).append(SEPARATOR);

        return methodBuilder.toString();
    }

    public String methodDeclaration(int baseIndentationInTabs) {
        return methodDeclarationBuilder(baseIndentationInTabs).toString();
    }

    private StringBuilder methodDeclarationBuilder(int baseIndentationInTabs) {
        StringBuilder methodBuilder = new StringBuilder();

        indent(methodBuilder, baseIndentationInTabs);
        methodModifier().ifPresent(methodModifier -> methodBuilder.append(methodModifier).append(" "));

        returnedType().ifPresent(type -> methodBuilder.append(type).append(" "));

        methodBuilder.append(getMethodName());
        methodBuilder.append(methodDeclaration.parameters().stream()
                .map(Object::toString)
                .collect(joining(", ", "(", ")")));
        return methodBuilder;
    }

    void applyFeaturesToMethodBody(Applicable applicable) {
        List<Feature> features = new FeatureProvider(astNodeFactory).features(applicable);
        for (int i = 0; i < body().size(); i++) {
            Object bodyNode = body().get(i);
            body().remove(bodyNode);
            for (Feature testMethodFeature : features) {
                bodyNode = testMethodFeature.apply(bodyNode);
            }
            body().add(i, bodyNode);
        }
    }

    protected abstract String methodSuffix();

    protected abstract String getMethodName();

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
                .filter(type -> !isVoid(type))
                .map(Object::toString);
    }

    protected abstract Optional<String> methodModifier();
}
