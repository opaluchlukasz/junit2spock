package com.github.opaluchlukasz.junit2spock.core.util

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.AST
import spock.lang.Shared
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.util.TypeUtil.isVoid
import static org.eclipse.jdt.core.dom.AST.*
import static org.eclipse.jdt.core.dom.AST.newAST
import static org.eclipse.jdt.core.dom.PrimitiveType.BOOLEAN
import static org.eclipse.jdt.core.dom.PrimitiveType.VOID

class TypeUtilTest extends Specification {
    private static final AST ast = newAST(JLS8)

    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })

    def 'should return false if type is not primitive'() {
        expect:
        !isVoid(nodeFactory.simpleType(nodeFactory.simpleName('Object')))
    }

    def 'should return false for primitive type other than void'() {
        expect:
        !isVoid(nodeFactory.primitiveType(BOOLEAN))
    }

    def 'should return true for void type'() {
        expect:
        isVoid(nodeFactory.primitiveType(VOID))
    }
}
