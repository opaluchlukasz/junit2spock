package com.github.opaluchlukasz.junit2spock.core.visitor;

import com.github.opaluchlukasz.junit2spock.core.model.InterfaceModelBuilder;
import com.github.opaluchlukasz.junit2spock.core.model.TypeModel;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class InterfaceVisitor extends ASTVisitor {

    private final InterfaceModelBuilder interfaceModelBuilder = new InterfaceModelBuilder();

    @Override
    public boolean visit(TypeDeclaration node) {
        interfaceModelBuilder.withInterfaceName(node.getName());
        interfaceModelBuilder.withSuperType(node.getSuperclassType());
        return true;
    }

    @Override
    public boolean visit(ImportDeclaration node) {
        interfaceModelBuilder.withImport(node);
        return false;
    }

    @Override
    public boolean visit(PackageDeclaration node) {
        interfaceModelBuilder.withPackageDeclaration(node);
        return false;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        interfaceModelBuilder.withField(node);
        return false;
    }

    @Override
    public boolean visit(MethodDeclaration methodDeclaration) {
        MethodVisitor visitor = new MethodVisitor();
        methodDeclaration.accept(visitor);
        interfaceModelBuilder.withMethod(visitor.methodModel());
        return false;
    }

    public TypeModel classModel() {
        return interfaceModelBuilder.build();
    }
}
