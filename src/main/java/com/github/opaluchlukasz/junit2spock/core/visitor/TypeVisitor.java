package com.github.opaluchlukasz.junit2spock.core.visitor;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.model.TypeModel;
import com.github.opaluchlukasz.junit2spock.core.model.TypeModelBuilder;
import com.github.opaluchlukasz.junit2spock.core.model.method.MethodModelFactory;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.Stack;

public class TypeVisitor extends ASTVisitor {

    private final ASTNodeFactory astNodeFactory;
    private final MethodModelFactory methodModelFactory;
    private final Stack<TypeModelBuilder> typeModelBuilders;

    TypeVisitor(MethodModelFactory methodModelFactory, ASTNodeFactory astNodeFactory) {
        this.methodModelFactory = methodModelFactory;
        this.astNodeFactory = astNodeFactory;
        typeModelBuilders = new Stack<>();
        typeModelBuilders.push(new TypeModelBuilder(astNodeFactory));
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        if (currentTypeModelBuilder().typeName() != null) {
            typeModelBuilders.push(new TypeModelBuilder(astNodeFactory));
        }
        currentTypeModelBuilder().withTypeName(node.getName())
                .withModifiers(node.modifiers())
                .withSuperType(node.getSuperclassType())
                .withIsInterface(node.isInterface());
        return true;
    }

    @Override
    public void endVisit(TypeDeclaration node) {
        if (typeModelBuilders.size() > 1) {
            TypeModel typeModel = typeModelBuilders.pop().build();
            currentTypeModelBuilder().withInnerType(typeModel);
        }
    }

    @Override
    public boolean visit(ImportDeclaration node) {
        currentTypeModelBuilder().withImport(node);
        return false;
    }

    @Override
    public boolean visit(PackageDeclaration node) {
        currentTypeModelBuilder().withPackageDeclaration(node);
        return false;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        currentTypeModelBuilder().withField(node);
        return false;
    }

    @Override
    public boolean visit(MethodDeclaration methodDeclaration) {
        currentTypeModelBuilder().withMethod(methodModelFactory.get(methodDeclaration));
        return false;
    }

    public TypeModel typeModel() {
        return currentTypeModelBuilder().build();
    }

    private TypeModelBuilder currentTypeModelBuilder() {
        return typeModelBuilders.peek();
    }
}
