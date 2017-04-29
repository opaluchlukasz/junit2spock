package com.github.opaluchlukasz.junit2spock.core;

import org.eclipse.jdt.core.dom.AST;
import org.springframework.stereotype.Component;

@Component
public class AstProxy implements AstProvider {
    private AST target;

    void setTarget(AST target) {
        this.target = target;
    }

    @Override
    public AST get() {
        return target;
    }
}
