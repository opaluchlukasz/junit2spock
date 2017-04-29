package com.github.opaluchlukasz.junit2spock.core;

import com.github.opaluchlukasz.junit2spock.core.model.TypeModel;
import com.github.opaluchlukasz.junit2spock.core.visitor.InterfaceVisitor;
import com.github.opaluchlukasz.junit2spock.core.visitor.TestClassVisitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static java.util.regex.Pattern.quote;
import static org.eclipse.jdt.core.dom.ASTParser.K_COMPILATION_UNIT;

@Component
public class Spocker {

    private final AstProxy astProxy;
    private final Supplier<TestClassVisitor> testClassVisitorSupplier;
    private final Supplier<InterfaceVisitor> interfaceVisitorSupplier;

    @Autowired
    public Spocker(AstProxy astProxy,
                   Supplier<TestClassVisitor> testClassVisitorSupplier,
                   Supplier<InterfaceVisitor> interfaceVisitorSupplier) {
        this.astProxy = astProxy;
        this.testClassVisitorSupplier = testClassVisitorSupplier;
        this.interfaceVisitorSupplier = interfaceVisitorSupplier;
    }

    public TypeModel toGroovyTypeModel(String source) {
        source = source.replaceAll("//(?i)\\s*" + quote("given") + SEPARATOR, "givenBlockStart();");
        source = source.replaceAll("//(?i)\\s*" + quote("when") + SEPARATOR, "whenBlockStart();");
        source = source.replaceAll("//(?i)\\s*" + quote("then") + SEPARATOR, "thenBlockStart();");

        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(source.toCharArray());
        parser.setKind(K_COMPILATION_UNIT);

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        astProxy.setTarget(cu.getAST());

        TypeDeclaration typeDeclaration = typeDeclaration(cu);

        if (typeDeclaration.isInterface()) {
            InterfaceVisitor visitor = interfaceVisitorSupplier.get();
            cu.accept(visitor);
            return visitor.classModel();
        } else {
            TestClassVisitor visitor = testClassVisitorSupplier.get();
            cu.accept(visitor);
            return visitor.classModel();
        }
    }

    private TypeDeclaration typeDeclaration(CompilationUnit cu) {
        List<TypeDeclaration> types = cu.types();
        return types.get(0);
    }
}
