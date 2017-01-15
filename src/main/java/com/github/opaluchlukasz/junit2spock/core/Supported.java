package com.github.opaluchlukasz.junit2spock.core;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public enum Supported {
    TEST_ANNOTATION(Test.class.getName(), false),
    ASSERT_EQUALS("org.junit.Assert.assertEquals", true);

    private String imported;
    private boolean testMethodFeature;

    Supported(String imported, boolean testMethodFeature) {
        this.imported = imported;
        this.testMethodFeature = testMethodFeature;
    }

    public static List<String> imports() {
        return stream(values()).map(supported -> supported.imported).collect(toList());
    }

    public static List<Supported> testMethodFeatures() {
        return stream(values()).filter(supported -> supported.testMethodFeature).collect(toList());
    }
}
