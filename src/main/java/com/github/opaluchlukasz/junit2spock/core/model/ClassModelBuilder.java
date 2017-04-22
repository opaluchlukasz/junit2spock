package com.github.opaluchlukasz.junit2spock.core.model;

import com.github.opaluchlukasz.junit2spock.core.model.method.MethodModel;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;

import java.util.LinkedList;
import java.util.List;

public class ClassModelBuilder {
    private List<ImportDeclaration> imports = new LinkedList<>();
    private List<MethodModel> methods = new LinkedList<>();
    private List<FieldDeclaration> fields = new LinkedList<>();
    private List<TypeModel> innerTypes = new LinkedList<>();
    private String className;
    private PackageDeclaration packageDeclaration;
    private Type superclassType;
    private List<Modifier> modifiers = new LinkedList<>();
    private AST ast;

    public ClassModelBuilder withClassName(SimpleName className) {
        this.className = className.getFullyQualifiedName();
        return this;
    }

    public String className() {
        return className;
    }

    public ClassModelBuilder withImport(ImportDeclaration node) {
        imports.add(node);
        return this;
    }

    public ClassModelBuilder withInnerType(TypeModel node) {
        innerTypes.add(node);
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

    public ClassModelBuilder withSuperType(Type superclassType) {
        this.superclassType = superclassType;
        return this;
    }

    public ClassModelBuilder withAST(AST ast) {
        this.ast = ast;
        return this;
    }

    public ClassModelBuilder withModifiers(List modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public TypeModel build() {
        return new ClassModel(className,
                superclassType,
                packageDeclaration,
                fields,
                methods,
                imports,
                ast,
                innerTypes,
                modifiers);
    }

}
