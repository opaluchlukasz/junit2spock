package com.github.opaluchlukasz.junit2spock.core.node;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodInvocation;

import static java.lang.String.format;

public final class SpockMockBehaviour {

    private final MethodInvocation methodInvocation;
    private final Block behaviour;

    public SpockMockBehaviour(MethodInvocation methodInvocation, Block behaviour) {
        this.methodInvocation = methodInvocation;
        this.behaviour = behaviour;
    }

    @Override
    public String toString() {
        return format("%s >> %s", methodInvocation, behaviour);
    }
}
