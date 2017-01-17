package com.github.opaluchlukasz.junit2spock.core;

import com.github.opaluchlukasz.junit2spock.core.model.ClassModel;
import com.github.opaluchlukasz.junit2spock.core.visitor.TestClassVisitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import static java.io.File.separator;
import static java.util.regex.Matcher.quoteReplacement;
import static org.eclipse.jdt.core.dom.ASTParser.K_COMPILATION_UNIT;

public class Spocker {

    private final ClassModel classModel;

    public Spocker(String source) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(source.toCharArray());
        parser.setKind(K_COMPILATION_UNIT);

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        TestClassVisitor visitor = new TestClassVisitor();
        cu.accept(visitor);
        classModel = visitor.classModel();
    }

    public String asGroovyClass() {
        return classModel.asGroovyClass();
    }

    public String getGroovyFilePath() {
        StringBuilder path = new StringBuilder();
        if (classModel.packageDeclaration != null) {
            path.append(classModel.packageDeclaration.getName().getFullyQualifiedName().replace(quoteReplacement("."), separator))
                    .append(separator);
        }
        path.append(classModel.className).append(".groovy");
        return path.toString();
    }
}
