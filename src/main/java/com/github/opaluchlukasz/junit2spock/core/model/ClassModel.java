package com.github.opaluchlukasz.junit2spock.core.model;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.SupportedJunitFeatures;
import com.github.opaluchlukasz.junit2spock.core.model.method.MethodModel;
import com.github.opaluchlukasz.junit2spock.core.model.method.TestMethodModel;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.spockframework.util.Immutable;
import spock.lang.Specification;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static java.util.Collections.unmodifiableList;

@Immutable
public class ClassModel implements TypeModel {

    private final String className;
    private final PackageDeclaration packageDeclaration;
    private final List<FieldDeclaration> fields;
    private final List<MethodModel> methods;
    private final List<ImportDeclaration> imports;
    private final Optional<String> superClassType;

    ClassModel(String className, Type superClassType, PackageDeclaration packageDeclaration,
               List<FieldDeclaration> fields, List<MethodModel> methods, List<ImportDeclaration> imports) {
        ASTNodeFactory astNodeFactory = new ASTNodeFactory();

        LinkedList<ImportDeclaration> importDeclarations = new LinkedList<>(imports);

        if (isTestClass(methods)) {
            this.superClassType = Optional.of(Specification.class.getSimpleName());
            importDeclarations.add(astNodeFactory.importDeclaration(Specification.class));
        } else {
            this.superClassType = Optional.ofNullable(superClassType).map(Object::toString);
        }

        this.className = className;
        this.packageDeclaration = packageDeclaration;
        this.fields = unmodifiableList(new LinkedList<>(fields));
        this.methods = unmodifiableList(new LinkedList<>(methods));
        this.imports = unmodifiableList(importDeclarations);
    }

    private boolean isTestClass(List<MethodModel> methods) {
        return methods.stream().anyMatch(methodModel -> methodModel instanceof TestMethodModel);
    }

    @Override
    public String asGroovyClass() {
        StringBuilder builder = new StringBuilder();
        Optional.ofNullable(packageDeclaration).ifPresent(builder::append);

        List<String> supported = SupportedJunitFeatures.imports();

        imports.stream()
                .filter(importDeclaration -> !supported.contains(importDeclaration.getName().getFullyQualifiedName()))
                .forEach(builder::append);

        builder.append(SEPARATOR).append("class ")
                .append(className);

        superClassType.ifPresent(superClass -> builder.append(" extends ").append(superClass));

        builder.append(" {")
                .append(SEPARATOR);

        fields.forEach(field -> builder.append(field.toString()));

        methods.forEach(methodModel -> builder.append(methodModel.asGroovyMethod(1)));

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
