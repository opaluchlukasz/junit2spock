package com.github.opaluchlukasz.junit2spock.core.model;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import spock.lang.Specification;

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

    public ClassModelBuilder withClassName(SimpleName className) {
        if (this.className != null) {
            throw new IllegalArgumentException("Inner classes not supported yet");
        }
        this.className = className.getFullyQualifiedName();
        return this;
    }

    public ClassModelBuilder withImport(ImportDeclaration node) {
        imports.add(node);
        return this;
    }

    public ClassModelBuilder withMethod(MethodModel methodModel) {
        methods.add(methodModel);
        return this;
    }

    public ClassModelBuilder withPackageDeclaration(PackageDeclaration packageDeclaration) {
        this.packageDeclaration = packageDeclaration;
        return this;
    }

    public ClassModelBuilder withField(FieldDeclaration node) {
        fields.add(node);
        return this;
    }

    public String build() {
        ASTNodeFactory astNodeFactory = new ASTNodeFactory();

        imports.add(astNodeFactory.importDeclaration(Specification.class));

        StringBuilder builder = new StringBuilder();
        Optional.ofNullable(packageDeclaration).ifPresent(builder::append);
        imports.forEach(builder::append);

        builder.append(SEPARATOR).append("class ")
                .append(className)
                .append(" extends ")
                .append(Specification.class.getSimpleName())
                .append(" {")
                .append(SEPARATOR);

        fields.forEach(field -> builder.append(field.toString()));

        methods.forEach(methodModel -> builder.append(methodModel.asGroovyMethod()));

        builder.append("}");

        builder.append(SEPARATOR);
        return builder.toString();
    }
}
