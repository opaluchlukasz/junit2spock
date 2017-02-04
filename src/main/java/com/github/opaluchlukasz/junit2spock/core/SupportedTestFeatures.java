package com.github.opaluchlukasz.junit2spock.core;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public enum SupportedTestFeatures {
    MOCKITO(of("org.mockito"), false),
    TEST_ANNOTATION(of("org.junit.Test"), false),
    ASSERT_EQUALS(of("org.junit.Assert.assertEquals", "junit.framework.Assert.assertEquals",
            "org.junit.Assert.assertArrayEquals", "junit.framework.Assert.assertArrayEquals"), true),
    ASSERT_NOT_NULL(of("org.junit.Assert.assertNotNull", "junit.framework.Assert.assertEquals"), true),
    ASSERT_TRUE(of("org.junit.Assert.assertTrue", "junit.framework.Assert.assertTrue"), true);

    private List<String> imports;
    private boolean testMethodFeature;

    SupportedTestFeatures(List<String> imports, boolean testMethodFeature) {
        this.imports = imports;
        this.testMethodFeature = testMethodFeature;
    }

    public static List<String> imports() {
        return stream(values())
                .flatMap(supported -> supported.imports.stream())
                .collect(toList());
    }

    public static List<SupportedTestFeatures> testMethodFeatures() {
        return stream(values())
                .filter(supported -> supported.testMethodFeature)
                .collect(toList());
    }
}
