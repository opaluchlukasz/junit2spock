package com.github.opaluchlukasz.junit2spock.core.model;

import com.github.opaluchlukasz.junit2spock.core.model.method.MethodModel;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.spockframework.util.Immutable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.SupportedTestFeature.imports;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static java.util.Collections.unmodifiableList;

@Immutable
public class InterfaceModel extends TypeModel {

    private final String typeName;
    private final PackageDeclaration packageDeclaration;
    private final List<FieldDeclaration> fields;
    private final List<MethodModel> methods;
    private final List<ImportDeclaration> imports;
    private final Optional<String> superClassType;

    InterfaceModel(String typeName, Type superClassType, PackageDeclaration packageDeclaration,
               List<FieldDeclaration> fields, List<MethodModel> methods, List<ImportDeclaration> imports) {

        LinkedList<ImportDeclaration> importDeclarations = new LinkedList<>(imports);


        this.superClassType = Optional.ofNullable(superClassType).map(Object::toString);

        this.typeName = typeName;
        this.packageDeclaration = packageDeclaration;
        this.fields = unmodifiableList(new LinkedList<>(fields));
        this.methods = unmodifiableList(new LinkedList<>(methods));
        this.imports = unmodifiableList(importDeclarations);
    }

    @Override
    public String asGroovyClass(int typeIndent) {
        StringBuilder builder = new StringBuilder();
        Optional.ofNullable(packageDeclaration).ifPresent(builder::append);

        List<String> supported = imports();

        imports.stream()
                .filter(importDeclaration -> !supported.contains(importDeclaration.getName().getFullyQualifiedName()))
                .forEach(builder::append);

        builder.append(SEPARATOR).append("interface ").append(typeName);

        superClassType.ifPresent(superClass -> builder.append(" extends ").append(superClass));

        builder.append(" {")
                .append(SEPARATOR);

        fields.forEach(field -> builder.append(field.toString()));

        methods.forEach(methodModel -> builder.append(methodModel.methodDeclaration(typeIndent + 1)).append(SEPARATOR));

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
        return typeName;
    }
}
