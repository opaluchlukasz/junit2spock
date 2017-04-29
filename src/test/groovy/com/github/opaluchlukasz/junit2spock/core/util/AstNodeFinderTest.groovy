package com.github.opaluchlukasz.junit2spock.core.util

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodInvocation
import spock.lang.Shared
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST

class AstNodeFinderTest extends Specification {

    private static final AST ast = newAST(JLS8)

    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })

    def 'should return empty for body element not being method invocation'() {
        expect:
        !methodInvocation(new Object(), 'someMethod').isPresent()
    }

    def 'should return empty for different method invocation'() {
        given:
        MethodInvocation otherMethodInvocation = nodeFactory.methodInvocation('someOtherMethod', [])

        expect:
        !methodInvocation(otherMethodInvocation, 'someMethod').isPresent()
    }
}
