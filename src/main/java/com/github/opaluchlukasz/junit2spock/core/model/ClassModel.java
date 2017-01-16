package com.github.opaluchlukasz.junit2spock.core.model;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.Supported;
import com.github.opaluchlukasz.junit2spock.core.model.method.MethodModel;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.spockframework.util.Immutable;
import spock.lang.Specification;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static java.util.Collections.unmodifiableList;

@Immutable
public class ClassModel {

    public final String className;
    public final PackageDeclaration packageDeclaration;
    public final List<FieldDeclaration> fields;
    public final List<MethodModel> methods;
    public final List<ImportDeclaration> imports;

    ClassModel(String className, PackageDeclaration packageDeclaration, List<FieldDeclaration> fields,
               List<MethodModel> methods, List<ImportDeclaration> imports) {
        ASTNodeFactory astNodeFactory = new ASTNodeFactory();

        LinkedList<ImportDeclaration> importDeclarations = new LinkedList<>(imports);
        importDeclarations.add(astNodeFactory.importDeclaration(Specification.class));

        this.className = className;
        this.packageDeclaration = packageDeclaration;
        this.fields = unmodifiableList(new LinkedList<>(fields));
        this.methods = unmodifiableList(new LinkedList<>(methods));
        this.imports = unmodifiableList(importDeclarations);
    }

    public String asGroovyClass() {
        StringBuilder builder = new StringBuilder();
        Optional.ofNullable(packageDeclaration).ifPresent(builder::append);

        List<String> supported = Supported.imports();

        imports.stream()
                .filter(importDeclaration -> !supported.contains(importDeclaration.getName().getFullyQualifiedName()))
                .forEach(builder::append);

        builder.append(SEPARATOR).append("class ")
                .append(className)
                .append(" extends ")
                .append(Specification.class.getSimpleName())
                .append(" {")
                .append(SEPARATOR);

        fields.forEach(field -> builder.append(field.toString()));

        methods.forEach(methodModel -> builder.append(methodModel.asGroovyMethod(1)));

        builder.append("}");

        builder.append(SEPARATOR);
        return builder.toString();
    }
}
