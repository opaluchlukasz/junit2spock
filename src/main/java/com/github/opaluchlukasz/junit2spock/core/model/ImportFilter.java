package com.github.opaluchlukasz.junit2spock.core.model;

import org.eclipse.jdt.core.dom.ImportDeclaration;

import java.util.function.Predicate;

import static java.util.Arrays.stream;

public class ImportFilter implements Predicate<ImportDeclaration> {

    private static final String[] DEFAULT_GROOVY_IMPORTS = new String[] {
        "java.io",
        "java.lang",
        "java.math.BigDecimal",
        "java.math.BigInteger",
        "java.net",
        "java.util"
    };

    @Override
    public boolean test(ImportDeclaration importDeclaration) {
        return importDeclaration.isStatic() ||
                stream(DEFAULT_GROOVY_IMPORTS)
                        .noneMatch(imp -> importDeclaration.getName().getFullyQualifiedName().startsWith(imp));
    }
}
