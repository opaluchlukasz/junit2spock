package com.github.opaluchlukasz.junit2spock.core.node;

import com.github.opaluchlukasz.junit2spock.core.groovism.Groovism;
import org.eclipse.jdt.core.dom.Block;

import static com.github.opaluchlukasz.junit2spock.core.groovism.GroovismChainProvider.provide;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public class GroovyClosure {

    static final int NODE_TYPE = -13;
    private final Block body;
    private final Groovism groovism;

    GroovyClosure(Block body) {
        this.body = body;
        this.groovism = provide();
    }

    @Override
    public String toString() {
        return (String) body.statements().stream()
                .map(statement -> format("\t%s", groovism.apply(statement.toString())))
                .collect(joining(SEPARATOR, "{\n\t\t", "\t\t}"));
    }
}
