package com.github.opaluchlukasz.junit2spock.core.model;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.model.method.MethodModel;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ClassModelBuilder {
    private final ASTNodeFactory astNodeFactory;
    private List<ImportDeclaration> imports = new LinkedList<>();
    private List<MethodModel> methods = new LinkedList<>();
    private List<FieldDeclaration> fields = new LinkedList<>();
    private List<TypeModel> innerTypes = new LinkedList<>();
    private String className;
    private PackageDeclaration packageDeclaration;
    private Type superclassType;
    private List<ASTNode> modifiers = new LinkedList<>();

    public ClassModelBuilder(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
    }

    public ClassModelBuilder withClassName(SimpleName className) {
        this.className = className.getFullyQualifiedName();
        return this;
    }

    public String className() {
        return className;
    }

    public ClassModelBuilder withImport(ImportDeclaration node) {
        Optional.of(node).filter(new ImportFilter()).ifPresent(imports::add);
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

    public ClassModelBuilder withModifiers(List<ASTNode> modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public TypeModel build() {
        return new ClassModel(astNodeFactory,
                className,
                superclassType,
                packageDeclaration,
                fields,
                methods,
                imports,
                innerTypes,
                modifiers);
    }
}
