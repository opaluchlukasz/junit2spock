package com.github.opaluchlukasz.junit2spock.core;

import org.eclipse.jdt.core.dom.AST;
import org.springframework.stereotype.Component;

import static org.eclipse.jdt.core.dom.AST.newAST;
import static org.eclipse.jdt.core.dom.AST.JLS8;

@Component
public class AstProxy implements AstProvider {
    private AST target;

    public AstProxy() {
        this.target = newAST(JLS8);
    }

    void setTarget(AST target) {
        this.target = target;
    }

    @Override
    public AST get() {
        return target;
    }
}
