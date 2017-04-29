package com.github.opaluchlukasz.junit2spock.core.feature.mockito

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.AST
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.MockitoVerifyNoMoreInteractionsFeature.VERIFY_NO_MORE_INTERACTIONS
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST

class MockitoVerifyNoMoreInteractionsFeatureTest extends Specification {

    @Subject private MockitoVerifyNoMoreInteractionsFeature mockitoVerifyNoMoreInteractionsFeature =
            new MockitoVerifyNoMoreInteractionsFeature(nodeFactory)
    private static final AST ast = newAST(JLS8)
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })

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
