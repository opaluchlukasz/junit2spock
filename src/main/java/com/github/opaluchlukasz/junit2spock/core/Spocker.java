package com.github.opaluchlukasz.junit2spock.core;

import com.github.opaluchlukasz.junit2spock.core.model.TypeModel;
import com.github.opaluchlukasz.junit2spock.core.visitor.TypeVisitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static java.util.regex.Pattern.quote;
import static org.eclipse.jdt.core.dom.ASTParser.K_COMPILATION_UNIT;

@Component
public class Spocker {

    private final AstProxy astProxy;
    private final Supplier<TypeVisitor> testClassVisitorSupplier;

    @Autowired
    public Spocker(AstProxy astProxy, Supplier<TypeVisitor> testClassVisitorSupplier) {
        this.astProxy = astProxy;
        this.testClassVisitorSupplier = testClassVisitorSupplier;
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

        TypeVisitor visitor = testClassVisitorSupplier.get();
        cu.accept(visitor);
        return visitor.typeModel();
    }
}
