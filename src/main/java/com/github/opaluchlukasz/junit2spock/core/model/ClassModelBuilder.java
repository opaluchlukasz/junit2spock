package com.github.opaluchlukasz.junit2spock.core.model;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.Util.SEPARATOR;

public class ClassModelBuilder {
    private List<ImportDeclaration> imports = new LinkedList<>();
    private List<MethodModel> methods = new LinkedList<>();
    private List<FieldDeclaration> fields = new LinkedList<>();
    private String className;
    private PackageDeclaration packageDeclaration;

    public void withClassName(SimpleName className) {
        if (this.className != null) {
            throw new IllegalArgumentException("Inner classes not supported yet");
        }
        this.className = className.getFullyQualifiedName();
    }

    public void withImport(ImportDeclaration node) {
        imports.add(node);
    }

    public void withMethod(MethodModel methodModel) {
        methods.add(methodModel);
    }

    public void withPackageDeclaration(PackageDeclaration packageDeclaration) {
        this.packageDeclaration = packageDeclaration;
    }

    public void withField(FieldDeclaration node) {
        fields.add(node);
    }

    public String build() {
        StringBuilder builder = new StringBuilder();
        Optional.ofNullable(packageDeclaration).ifPresent(builder::append);
        imports.forEach(builder::append);

        builder.append(SEPARATOR).append("class ").append(className).append(" implements Specification {").append(SEPARATOR);

        fields.forEach(field -> builder.append(field.toString()));

        methods.forEach(methodModel -> builder.append(methodModel.asGroovyMethod()));

        builder.append(SEPARATOR).append("}");

        builder.append(SEPARATOR);
        return builder.toString();
    }

}
