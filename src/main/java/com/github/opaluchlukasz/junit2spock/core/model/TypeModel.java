package com.github.opaluchlukasz.junit2spock.core.model;

import java.util.Optional;

public interface TypeModel {
    String asGroovyClass(int typeIndent);
    Optional<String> packageDeclaration();
    String typeName();
}
