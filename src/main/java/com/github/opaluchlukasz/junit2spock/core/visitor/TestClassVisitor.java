package com.github.opaluchlukasz.junit2spock.core.visitor;

import com.github.opaluchlukasz.junit2spock.core.model.ClassModelBuilder;
import com.github.opaluchlukasz.junit2spock.core.model.TypeModel;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.Stack;

public class TestClassVisitor extends ASTVisitor {

    private final Stack<ClassModelBuilder> classModelBuilders;

    public TestClassVisitor() {
        classModelBuilders = new Stack<>();
        classModelBuilders.push(new ClassModelBuilder());
    }

    private ClassModelBuilder currentClassModelBuilder() {
        return classModelBuilders.peek();
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        if (currentClassModelBuilder().className() != null) {
            classModelBuilders.push(new ClassModelBuilder());
        }
        currentClassModelBuilder().withAST(node.getAST());
        currentClassModelBuilder().withClassName(node.getName());
        currentClassModelBuilder().withModifiers(node.modifiers());
        currentClassModelBuilder().withSuperType(node.getSuperclassType());
        return true;
    }

    @Override
    public void endVisit(TypeDeclaration node) {
        if (classModelBuilders.size() > 1) {
            TypeModel typeModel = classModelBuilders.pop().build();
            currentClassModelBuilder().withInnerType(typeModel);
        }
    }

    @Override
    public boolean visit(ImportDeclaration node) {
        currentClassModelBuilder().withImport(node);
        return false;
    }

    @Override
    public boolean visit(PackageDeclaration node) {
        currentClassModelBuilder().withPackageDeclaration(node);
        return false;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        currentClassModelBuilder().withField(node);
        return false;
    }

    @Override
    public boolean visit(MethodDeclaration methodDeclaration) {
        MethodVisitor visitor = new MethodVisitor();
        methodDeclaration.accept(visitor);
        currentClassModelBuilder().withMethod(visitor.methodModel());
        return false;
    }

    public TypeModel classModel() {
        return currentClassModelBuilder().build();
    }
}
