package com.github.opaluchlukasz.junit2spock.core.visitor;

import com.github.opaluchlukasz.junit2spock.core.model.ClassModelBuilder;
import com.github.opaluchlukasz.junit2spock.core.model.TypeModel;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class TestClassVisitor extends ASTVisitor {

    private final ClassModelBuilder classModelBuilder = new ClassModelBuilder();

    @Override
    public boolean visit(TypeDeclaration node) {
        classModelBuilder.withAST(node.getAST());
        classModelBuilder.withClassName(node.getName());
        classModelBuilder.withSuperType(node.getSuperclassType());
        return true;
    }

    @Override
    public boolean visit(ImportDeclaration node) {
        classModelBuilder.withImport(node);
        return false;
    }

    @Override
    public boolean visit(PackageDeclaration node) {
        classModelBuilder.withPackageDeclaration(node);
        return false;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        classModelBuilder.withField(node);
        return false;
    }

    @Override
    public boolean visit(MethodDeclaration methodDeclaration) {
        MethodVisitor visitor = new MethodVisitor();
        methodDeclaration.accept(visitor);
        classModelBuilder.withMethod(visitor.methodModel());
        return false;
    }

    public TypeModel classModel() {
        return classModelBuilder.build();
    }
}
