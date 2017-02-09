package com.github.opaluchlukasz.junit2spock.core.model.method

import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodDeclaration
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod
import static com.github.opaluchlukasz.junit2spock.core.model.method.MethodModelFactory.get
import static org.eclipse.jdt.core.dom.AST.JLS8

class MethodModelFactoryTest extends Specification {

    def 'should return TestMethodModel for test method'() {
        expect:
        get(methodAnnotatedWith('Test')) instanceof TestMethodModel
    }

    @Unroll
    def 'should return FixtureMethodModel for fixture method (#methodName)'() {
        when:
        MethodModel methodModel = get(methodAnnotatedWith(annotation))

        then:
        methodModel instanceof FixtureMethodModel
        methodModel.methodName == methodName

        where:
        annotation    | methodName
        'Before'      | 'setup'
        'After'       | 'cleanup'
        'BeforeClass' | 'setupSpec'
        'AfterClass'  | 'cleanupSpec'
    }

    private static MethodDeclaration methodAnnotatedWith(String annotationName) {
        AST ast = AST.newAST(JLS8)
        def testAnnotation = ast.newMarkerAnnotation()
        testAnnotation.setTypeName(ast.newName(annotationName))
        aMethod(ast).withAnnotation(testAnnotation).build()
    }
}
