package com.github.opaluchlukasz.junit2spock.core.model;

import java.util.Optional;

import static java.io.File.separator;
import static java.util.regex.Matcher.quoteReplacement;

public abstract class TypeModel {
    public abstract String asGroovyClass(int typeIndent);
    public abstract String typeName();
    abstract Optional<String> packageDeclaration();

    public String outputFilePath() {
        StringBuilder path = new StringBuilder();
        packageDeclaration()
                .ifPresent(declaration -> path.append(declaration.replace(quoteReplacement("."), separator))
                        .append(separator));
        path.append(typeName()).append(".groovy");
        return path.toString();
    }
}
