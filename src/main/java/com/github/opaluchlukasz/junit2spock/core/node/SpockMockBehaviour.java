package com.github.opaluchlukasz.junit2spock.core.node;

import com.github.opaluchlukasz.junit2spock.core.groovism.Groovism;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodInvocation;

import static com.github.opaluchlukasz.junit2spock.core.groovism.GroovismChainProvider.provide;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public final class SpockMockBehaviour {

    private final MethodInvocation methodInvocation;
    private final Block behaviour;
    private final Groovism groovism;

    public SpockMockBehaviour(MethodInvocation methodInvocation, Block behaviour) {
        this.methodInvocation = methodInvocation;
        this.behaviour = behaviour;
        this.groovism = provide();
    }

    @Override
    public String toString() {
        return format("%s >> %s", methodInvocation, toString(behaviour));
    }

    private Object toString(Block behaviour) {
        return behaviour.statements().stream()
                .map(statement -> format("\t%s", groovism.apply(statement.toString())))
                .collect(joining(SEPARATOR, "{\n\t\t", "\t\t}"));
    }
}
