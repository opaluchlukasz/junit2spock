package com.github.opaluchlukasz.junit2spock.core.node.wrapper;

import com.github.opaluchlukasz.junit2spock.core.Applicable;
import com.github.opaluchlukasz.junit2spock.core.groovism.Groovism;

import java.util.function.Consumer;

import static com.github.opaluchlukasz.junit2spock.core.groovism.GroovismChainProvider.provide;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.indentation;

public class BaseWrapper {

    private final int indentationLevel;
    private final Applicable applicable;
    private final Groovism groovism;

    BaseWrapper(int indentationLevel, Applicable applicable) {
        this.indentationLevel = indentationLevel;
        this.applicable = applicable;
        this.groovism = provide();
    }

    int indentationLevel() {
        return indentationLevel;
    }

    protected Applicable applicable() {
        return applicable;
    }

    Consumer printStatement(StringBuilder builder) {
        return stmt -> {
            if (stmt instanceof BaseWrapper) {
                builder.append(stmt.toString());
            } else {
                builder.append(indentation(indentationLevel + 2)).append(groovism.apply(stmt.toString()));
                if (!stmt.toString().endsWith("\n")) {
                    builder.append(SEPARATOR);
                }
            }
        };
    }
}
