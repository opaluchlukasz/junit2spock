package com.github.opaluchlukasz.junit2spock.core;

import java.util.List;

import static com.github.opaluchlukasz.junit2spock.core.Applicable.FIELD_FEATURE;
import static com.github.opaluchlukasz.junit2spock.core.Applicable.TEST_METHOD;
import static com.google.common.collect.ImmutableList.of;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public enum SupportedTestFeature {
    MOCK_DECLARATION(of("org.mockito.Mock"), of(FIELD_FEATURE)),
    BEFORE(of("org.junit.Before"), emptyList()),
    BEFORE_CLASS(of("org.junit.BeforeClass"), emptyList()),
    AFTER(of("org.junit.After"), emptyList()),
    AFTER_CLASS(of("org.junit.AfterClass"), emptyList()),
    THEN_RETURN(emptyList(), of(TEST_METHOD)),
    TEST_ANNOTATION(of("org.junit.Test"), emptyList()),
    ASSERT_EQUALS(of("org.junit.Assert.assertEquals", "junit.framework.Assert.assertEquals",
            "org.junit.Assert.assertArrayEquals", "junit.framework.Assert.assertArrayEquals"), of(TEST_METHOD)),
    ASSERT_NOT_NULL(of("org.junit.Assert.assertNotNull", "junit.framework.Assert.assertNotNull"), of(TEST_METHOD)),
    ASSERT_NULL(of("org.junit.Assert.assertNull", "junit.framework.Assert.assertNull"), of(TEST_METHOD)),
    ASSERT_TRUE(of("org.junit.Assert.assertTrue", "junit.framework.Assert.assertTrue"), of(TEST_METHOD)),
    ASSERT_FALSE(of("org.junit.Assert.assertFalse", "junit.framework.Assert.assertFalse"), of(TEST_METHOD));

    private final List<String> imports;
    private final List<Applicable> applicables;

    SupportedTestFeature(List<String> imports, List<Applicable> applicables) {
        this.imports = imports;
        this.applicables = applicables;
    }

    public static List<String> imports() {
        return stream(values())
                .flatMap(supported -> supported.imports.stream())
                .collect(toList());
    }

    public static List<SupportedTestFeature> features(Applicable applicable) {
        return stream(values())
                .filter(supported -> supported.applicables.contains(applicable))
                .collect(toList());
    }
}

