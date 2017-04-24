package com.github.opaluchlukasz.junit2spock.core.model;

import com.github.opaluchlukasz.junit2spock.core.model.method.MethodModel;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class InterfaceModelBuilder {
    private List<ImportDeclaration> imports = new LinkedList<>();
    private List<MethodModel> methods = new LinkedList<>();
    private List<FieldDeclaration> fields = new LinkedList<>();
    private String interfaceName;
    private PackageDeclaration packageDeclaration;
    private Type superclassType;

    public InterfaceModelBuilder withInterfaceName(SimpleName className) {
        this.interfaceName = className.getFullyQualifiedName();
        return this;
    }

    public InterfaceModelBuilder withImport(ImportDeclaration node) {
        Optional.of(node).filter(new ImportFilter()).ifPresent(imports::add);
        return this;
    }

    public InterfaceModelBuilder withMethod(MethodModel methodModel) {
        methods.add(methodModel);
        return this;
    }

    public InterfaceModelBuilder withPackageDeclaration(PackageDeclaration packageDeclaration) {
        this.packageDeclaration = packageDeclaration;
        return this;
    }

    public InterfaceModelBuilder withField(FieldDeclaration node) {
        fields.add(node);
        return this;
    }

    public InterfaceModelBuilder withSuperType(Type superclassType) {
        this.superclassType = superclassType;
        return this;
    }

    public TypeModel build() {
        return new InterfaceModel(interfaceName, superclassType, packageDeclaration, fields, methods, imports);
    }
}
