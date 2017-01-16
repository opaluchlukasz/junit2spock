package com.github.opaluchlukasz.junit2spock.core.model.method.feature

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.MethodInvocation
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.opaluchlukasz.junit2spock.core.model.method.feature.AssertEqualsFeature.ASSERT_EQUALS

class AssertEqualsFeatureTest extends Specification {

    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory()

    @Subject AssertEqualsFeature assertEqualsFeature = new AssertEqualsFeature(nodeFactory)

    def 'should return false for non assertEquals method invocation'() {
        expect:
        !assertEqualsFeature.applicable(node)

        where:
        node << [new Object(), nodeFactory.methodInvocation('someMethod', [])]
    }

    def 'should return true for assertEquals method invocation'() {
        given:
        MethodInvocation methodInvocation = nodeFactory.methodInvocation(ASSERT_EQUALS,
                [nodeFactory.numberLiteral("0"), nodeFactory.numberLiteral("0")])

        expect:
        assertEqualsFeature.applicable(methodInvocation)
    }
}
