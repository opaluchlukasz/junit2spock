package com.github.opaluchlukasz.junit2spock.core.model;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.SupportedTestFeature;
import com.github.opaluchlukasz.junit2spock.core.feature.Feature;
import com.github.opaluchlukasz.junit2spock.core.feature.FeatureProvider;
import com.github.opaluchlukasz.junit2spock.core.groovism.Groovism;
import com.github.opaluchlukasz.junit2spock.core.model.method.MethodModel;
import com.github.opaluchlukasz.junit2spock.core.model.method.TestMethodModel;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Type;
import spock.lang.Specification;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.Applicable.FIELD_FEATURE;
import static com.github.opaluchlukasz.junit2spock.core.groovism.GroovismChainProvider.provide;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.indent;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.indentation;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

public class ClassModel extends TypeModel {

    private final ASTNodeFactory astNodeFactory;
    private final String className;
    private final Optional<Type> superClassType;
    private final PackageDeclaration packageDeclaration;
    private final List<FieldDeclaration> fields;
    private final List<MethodModel> methods;
    private final List<ImportDeclaration> imports;
    private final List<TypeModel> innerTypes;
    private final List<ASTNode> modifiers;
    private final Groovism groovism;

    // CHECKSTYLE.OFF: ParameterNumber
    ClassModel(ASTNodeFactory astNodeFactory, String className, Type superClassType, PackageDeclaration packageDeclaration,
               List<FieldDeclaration> fields, List<MethodModel> methods, List<ImportDeclaration> imports,
               List<TypeModel> innerTypes, List<ASTNode> modifiers) {
        groovism = provide();

        this.astNodeFactory = astNodeFactory;
        this.className = className;
        this.packageDeclaration = packageDeclaration;
        this.fields = fieldDeclarations(fields);
        this.methods = unmodifiableList(new LinkedList<>(methods));
        this.modifiers = unmodifiableList(new LinkedList<>(modifiers));
        this.imports = imports;
        this.innerTypes = unmodifiableList(new LinkedList<>(innerTypes));
        if (isTestClass(methods)) {
            this.superClassType = Optional.of(astNodeFactory
                    .simpleType(astNodeFactory.simpleName(Specification.class.getSimpleName())));
            imports.add(astNodeFactory.importDeclaration(Specification.class));
        } else {
            this.superClassType = Optional.ofNullable(superClassType);
        }
    }
    // CHECKSTYLE.ON: ParameterNumber

    private List<FieldDeclaration> fieldDeclarations(List<FieldDeclaration> fieldDeclarations) {
        List<FieldDeclaration> result = new LinkedList<>();
        List<Feature> features = new FeatureProvider(astNodeFactory).features(FIELD_FEATURE);
        for (FieldDeclaration fieldDeclaration : fieldDeclarations) {
            FieldDeclaration toApply = fieldDeclaration;
            for (Feature feature : features) {
                toApply = (FieldDeclaration) feature.apply(fieldDeclaration);
            }
            result.add(toApply);
        }
        return result;
    }

    private boolean isTestClass(List<MethodModel> methods) {
        return methods.stream().anyMatch(methodModel -> methodModel instanceof TestMethodModel);
    }

    @Override
    public String asGroovyClass(int classIndent) {
        StringBuilder builder = new StringBuilder();
        Optional.ofNullable(packageDeclaration)
                .map(declaration -> groovism.apply(declaration.toString()))
                .ifPresent(builder::append);

        List<String> supported = SupportedTestFeature.imports();

        imports.stream()
                .filter(importDeclaration -> !supported.contains(importDeclaration.getName().getFullyQualifiedName()))
                .map(declaration -> groovism.apply(declaration.toString()))
                .forEach(builder::append);

        builder.append(SEPARATOR);
        indent(builder, classIndent);

        builder.append(groovism.apply(modifiers.stream().map(Object::toString).collect(joining(" ", "", " "))));
        builder.append("class ")
                .append(className);

        superClassType.ifPresent(superClass -> builder.append(" extends ").append(superClass));

        builder.append(" {")
                .append(SEPARATOR);

        fields.forEach(field -> builder.append(indentation(classIndent + 1)).append(groovism.apply(field.toString())));

        builder.append(SEPARATOR);

        methods.forEach(methodModel -> builder.append(methodModel.asGroovyMethod(classIndent + 1)));

        innerTypes.forEach(classModel -> builder.append(classModel.asGroovyClass(classIndent + 1)));

        indent(builder, classIndent);
        builder.append("}");

        builder.append(SEPARATOR);
        return builder.toString();
    }

    @Override
    public Optional<String> packageDeclaration() {
        return Optional.ofNullable(packageDeclaration)
                .map(declaration -> declaration.getName().getFullyQualifiedName());
    }

    @Override
    public String typeName() {
        return className;
    }
}
