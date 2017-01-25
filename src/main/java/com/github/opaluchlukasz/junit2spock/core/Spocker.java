package com.github.opaluchlukasz.junit2spock.core;

import com.github.opaluchlukasz.junit2spock.core.model.TypeModel;
import com.github.opaluchlukasz.junit2spock.core.visitor.InterfaceVisitor;
import com.github.opaluchlukasz.junit2spock.core.visitor.TestClassVisitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.List;

import static java.io.File.separator;
import static java.util.regex.Matcher.quoteReplacement;
import static org.eclipse.jdt.core.dom.ASTParser.K_COMPILATION_UNIT;

public class Spocker {

    private final TypeModel typeModel;

    public Spocker(String source) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(source.toCharArray());
        parser.setKind(K_COMPILATION_UNIT);

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        TypeDeclaration typeDeclaration = typeDeclaration(cu);

        if (typeDeclaration.isInterface()) {
            InterfaceVisitor visitor = new InterfaceVisitor();
            cu.accept(visitor);
            typeModel = visitor.classModel();
        } else {
            TestClassVisitor visitor = new TestClassVisitor();
            cu.accept(visitor);
            typeModel = visitor.classModel();
        }
    }

    private TypeDeclaration typeDeclaration(CompilationUnit cu) {
        List<TypeDeclaration> types = cu.types();
        if (types.size() > 1) {
            throw new IllegalArgumentException("Inner types not supported");
        }
        return types.get(0);
    }

    public String asGroovyClass() {
        return typeModel.asGroovyClass();
    }

    public String outputFilePath() {
        StringBuilder path = new StringBuilder();
        typeModel.packageDeclaration()
                .ifPresent(declaration -> path.append(declaration.replace(quoteReplacement("."), separator))
                        .append(separator));
        path.append(typeModel.typeName()).append(".groovy");
        return path.toString();
    }
}
