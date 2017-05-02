package com.github.opaluchlukasz.junit2spock.core.feature

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.AST
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.github.opaluchlukasz.junit2spock.core.SupportedTestFeature.TEST_ANNOTATION
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST

class FeatureFactoryTest extends Specification {

    private static final AST ast = newAST(JLS8)
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })
    @Subject private FeatureFactory featureFactory = new FeatureFactory(nodeFactory)

    def 'should throw an exception for unsupported test feature'() {
        when:
        featureFactory.provide(TEST_ANNOTATION)

        then:
        UnsupportedOperationException ex = thrown()
        ex.message == "Unsupported feature: ${TEST_ANNOTATION.name()}"
    }
}
