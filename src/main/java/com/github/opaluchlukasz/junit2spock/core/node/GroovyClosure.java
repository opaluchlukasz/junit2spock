package com.github.opaluchlukasz.junit2spock.core.node;

import com.github.opaluchlukasz.junit2spock.core.groovism.Groovism;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import java.util.Arrays;

import static com.github.opaluchlukasz.junit2spock.core.groovism.GroovismChainProvider.provide;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public class GroovyClosure {

    static final int NODE_TYPE = -13;
    private final Block body;
    private final SingleVariableDeclaration[] arguments;
    private final Groovism groovism;

    GroovyClosure(Block body, SingleVariableDeclaration... arguments) {
        this.body = body;
        this.arguments = arguments;
        this.groovism = provide();
    }

    @Override
    public String toString() {
        return (String) body.statements().stream()
                .map(statement -> format("\t%s", groovism.apply(statement.toString())))
                .collect(joining(SEPARATOR, prefix(), "\t\t}"));
    }

    private String prefix() {
        return arguments.length == 0 ? "{\n\t\t" : Arrays.stream(arguments)
                .map(Object::toString)
                .collect(joining(", ", "{ ", " ->\n\t\t"));
    }
}
