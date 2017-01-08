package com.github.opaluchlukasz.junit2spock.core;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public enum Supported {
    TEST_ANNOTATION(Test.class.getName());

    private String imported;

    Supported(String imported) {
        this.imported = imported;
    }

    public static List<String> imports() {
        return stream(values()).map(supported -> supported.imported).collect(toList());
    }
}
