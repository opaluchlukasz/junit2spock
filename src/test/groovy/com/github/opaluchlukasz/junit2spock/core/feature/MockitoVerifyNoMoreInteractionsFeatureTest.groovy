package com.github.opaluchlukasz.junit2spock.core.feature

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.opaluchlukasz.junit2spock.core.feature.MockitoVerifyNoMoreInteractionsFeature.VERIFY_NO_MORE_INTERACTIONS

class MockitoVerifyNoMoreInteractionsFeatureTest extends Specification {

    @Subject private MockitoVerifyNoMoreInteractionsFeature mockitoVerifyNoMoreInteractionsFeature =
            new MockitoVerifyNoMoreInteractionsFeature(nodeFactory)
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory()

    def 'should return false for non verifyNoMoreInteractions method invocation'() {
        expect:
        !mockitoVerifyNoMoreInteractionsFeature.applicable(node).isPresent()

        where:
        node << [new Object(),
                 nodeFactory.methodInvocation('someMethod', []),
                 nodeFactory.expressionStatement(nodeFactory.methodInvocation(VERIFY_NO_MORE_INTERACTIONS, [])),
                 nodeFactory.methodInvocation(VERIFY_NO_MORE_INTERACTIONS, [])]
    }

    def 'should return true for verifyNoMoreInteractions verify method invocation'() {
        expect:
        mockitoVerifyNoMoreInteractionsFeature.applicable(astNode).isPresent()

        where:
        astNode << [nodeFactory.methodInvocation(VERIFY_NO_MORE_INTERACTIONS, [nodeFactory.simpleName('mockedObject')]),
                    nodeFactory.expressionStatement(nodeFactory.methodInvocation(VERIFY_NO_MORE_INTERACTIONS, [nodeFactory.simpleName('mockedObject')]))]
    }

    def 'should return Spock\'s mock no more interactions'() {
        expect:
        mockitoVerifyNoMoreInteractionsFeature.apply(astNode).toString() == '0 * mockedObject._'

        where:
        astNode << [nodeFactory.methodInvocation(VERIFY_NO_MORE_INTERACTIONS, [nodeFactory.simpleName('mockedObject')]),
                    nodeFactory.expressionStatement(nodeFactory.methodInvocation(VERIFY_NO_MORE_INTERACTIONS, [nodeFactory.simpleName('mockedObject')]))]
    }
}
