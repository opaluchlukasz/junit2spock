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

public class TypeModelBuilder {
    private List<ImportDeclaration> imports = new LinkedList<>();
    private List<MethodModel> methods = new LinkedList<>();
    private List<FieldDeclaration> fields = new LinkedList<>();
    private PackageDeclaration packageDeclaration;
    private Type superclassType;
    private final ASTNodeFactory astNodeFactory;
    private List<TypeModel> innerTypes = new LinkedList<>();
    private String typeName;
    private List<ASTNode> modifiers = new LinkedList<>();
    private boolean isInterface;

    public TypeModelBuilder(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
    }

    public TypeModelBuilder withTypeName(SimpleName typeName) {
        this.typeName = typeName.getFullyQualifiedName();
        return this;
    }

    public TypeModelBuilder withImport(ImportDeclaration node) {
        Optional.of(node).filter(new ImportFilter()).ifPresent(imports::add);
        return this;
    }

    public TypeModelBuilder withMethod(MethodModel methodModel) {
        methods.add(methodModel);
        return this;
    }

    public TypeModelBuilder withPackageDeclaration(PackageDeclaration packageDeclaration) {
        this.packageDeclaration = packageDeclaration;
        return this;
    }

    public TypeModelBuilder withField(FieldDeclaration node) {
        fields.add(node);
        return this;
    }

    public TypeModelBuilder withSuperType(Type superclassType) {
        this.superclassType = superclassType;
        return this;
    }

    public TypeModelBuilder withInnerType(TypeModel node) {
        innerTypes.add(node);
        return this;
    }

    public TypeModelBuilder withModifiers(List<ASTNode> modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public TypeModelBuilder withIsInterface(boolean isInterface) {
        this.isInterface = isInterface;
        return this;
    }

    public String typeName() {
        return typeName;
    }

    public TypeModel build() {
        if (isInterface) {
            return new InterfaceModel(typeName, superclassType, packageDeclaration, fields, methods, imports, modifiers);
        } else {
            return new ClassModel(astNodeFactory,
                    typeName,
                    superclassType,
                    packageDeclaration,
                    fields,
                    methods,
                    imports,
                    innerTypes,
                    modifiers);
        }
    }
}
