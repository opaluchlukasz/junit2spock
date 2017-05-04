package com.github.opaluchlukasz.junit2spock.core;

import java.util.List;
import java.util.Set;

import static com.github.opaluchlukasz.junit2spock.core.Applicable.FIELD_FEATURE;
import static com.github.opaluchlukasz.junit2spock.core.Applicable.FIXTURE_METHOD;
import static com.github.opaluchlukasz.junit2spock.core.Applicable.REGULAR_METHOD;
import static com.github.opaluchlukasz.junit2spock.core.Applicable.TEST_METHOD;
import static com.google.common.collect.ImmutableSet.of;
import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;

public enum SupportedTestFeature {
    MOCK_ANNOTATION(of("org.mockito.Mock"), of(FIELD_FEATURE)),
    MOCK_METHOD(of("org.mockito.Mockito.mock"), of(FIXTURE_METHOD, TEST_METHOD, REGULAR_METHOD)),
    MOCKITO_VERIFY(of(
            "org.mockito.Mockito.verify",
            "org.mockito.Mockito.times",
            "org.mockito.Mockito.never",
            "org.mockito.Mockito.atLeast",
            "org.mockito.Mockito.atMost",
            "org.mockito.Mockito.atLeastOnce",
            "org.mockito.Matchers.any",
            "org.mockito.Matchers.anyObject",
            "org.mockito.Matchers.anyBoolean",
            "org.mockito.Matchers.anyByte",
            "org.mockito.Matchers.anyChar",
            "org.mockito.Matchers.anyInt",
            "org.mockito.Matchers.anyLong",
            "org.mockito.Matchers.anyFloat",
            "org.mockito.Matchers.anyDouble",
            "org.mockito.Matchers.anyShort",
            "org.mockito.Matchers.anyString",
            "org.mockito.Matchers.anyObject",
            "org.mockito.Matchers.anyList",
            "org.mockito.Matchers.anySet",
            "org.mockito.Matchers.anyMap",
            "org.mockito.Matchers.anyCollection",
            "org.mockito.Matchers.anyIterable"), of(TEST_METHOD)),
    MOCKITO_VERIFY_NO_MORE_INTERACTIONS(of("org.mockito.Mockito.verifyNoMoreInteractions"), of(TEST_METHOD)),
    BEFORE(of("org.junit.Before"), emptySet()),
    BEFORE_CLASS(of("org.junit.BeforeClass"), emptySet()),
    AFTER(of("org.junit.After"), emptySet()),
    AFTER_CLASS(of("org.junit.AfterClass"), emptySet()),
    THEN_RETURN(of("org.mockito.Mockito.when"), of(FIXTURE_METHOD, TEST_METHOD, REGULAR_METHOD)),
    WILL_RETURN(of("org.mockito.BDDMockito.given"), of(FIXTURE_METHOD, TEST_METHOD, REGULAR_METHOD)),
    THEN_THROW(emptySet(), of(FIXTURE_METHOD, TEST_METHOD, REGULAR_METHOD)),
    TEST_ANNOTATION(of("org.junit.Test"), emptySet()),
    ASSERT_EQUALS(of("org.junit.Assert.assertEquals", "junit.framework.Assert.assertEquals",
            "org.junit.Assert.assertArrayEquals", "junit.framework.Assert.assertArrayEquals"), of(TEST_METHOD)),
    ASSERT_NOT_NULL(of("org.junit.Assert.assertNotNull", "junit.framework.Assert.assertNotNull"), of(TEST_METHOD)),
    ASSERT_NULL(of("org.junit.Assert.assertNull", "junit.framework.Assert.assertNull"), of(TEST_METHOD)),
    ASSERT_TRUE(of("org.junit.Assert.assertTrue", "junit.framework.Assert.assertTrue"), of(TEST_METHOD)),
    ASSERT_FALSE(of("org.junit.Assert.assertFalse", "junit.framework.Assert.assertFalse"), of(TEST_METHOD));

    private final Set<String> imports;
    private final Set<Applicable> applicables;

    SupportedTestFeature(Set<String> imports, Set<Applicable> applicables) {
        this.imports = imports;
        this.applicables = applicables;
    }

    public static List<String> imports() {
        return stream(values())
                .flatMap(supported -> supported.imports.stream())
                .collect(toList());
    }

    public static List<SupportedTestFeature> featuresTypes(Applicable applicable) {
        return stream(values())
                .filter(supported -> supported.applicables.contains(applicable))
                .collect(toList());
    }
}

