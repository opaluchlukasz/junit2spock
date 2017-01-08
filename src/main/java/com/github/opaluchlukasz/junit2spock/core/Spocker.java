package com.github.opaluchlukasz.junit2spock.core;

import com.github.opaluchlukasz.junit2spock.core.visitor.TestClassVisitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import static org.eclipse.jdt.core.dom.ASTParser.K_COMPILATION_UNIT;

public class Spocker {

    public String parse(String source) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(source.toCharArray());
        parser.setKind(K_COMPILATION_UNIT);

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        TestClassVisitor visitor = new TestClassVisitor();
        cu.accept(visitor);

        return visitor.asGroovyClass();
    }
}
