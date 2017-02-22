package com.github.opaluchlukasz.junit2spock.core.util

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.util.TypeUtil.isVoid
import static org.eclipse.jdt.core.dom.PrimitiveType.BOOLEAN
import static org.eclipse.jdt.core.dom.PrimitiveType.VOID

class TypeUtilTest extends Specification {

    private ASTNodeFactory astNodeFactory = new ASTNodeFactory()

    def 'should return false if type is not primitive'() {
        expect:
        !isVoid(astNodeFactory.simpleType(astNodeFactory.simpleName('Object')))
    }

    def 'should return false for primitive type other than void'() {
        expect:
        !isVoid(astNodeFactory.primitiveType(BOOLEAN))
    }

    def 'should return true for void type'() {
        expect:
        isVoid(astNodeFactory.primitiveType(VOID))
    }
}
