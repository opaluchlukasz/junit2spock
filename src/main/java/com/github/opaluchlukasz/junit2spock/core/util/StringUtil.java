package com.github.opaluchlukasz.junit2spock.core.util;

import java.util.stream.IntStream;

public final class StringUtil {
    public static final String SEPARATOR = System.getProperty("line.separator");

    private StringUtil() {
        //NOOP
    }

    public static StringBuilder indent(StringBuilder stringBuilder, int indentationInTabs) {
        IntStream.rangeClosed(1, indentationInTabs).forEach(__ -> stringBuilder.append("\t"));
        return stringBuilder;
    }

    public static String indentation(int indentationInTabs) {
        return IntStream.rangeClosed(1, indentationInTabs).mapToObj(__ -> "\t").reduce("", (a, b) ->  a + b);
    }
}
