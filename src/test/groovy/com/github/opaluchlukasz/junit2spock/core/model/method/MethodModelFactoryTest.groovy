package com.github.opaluchlukasz.junit2spock.core.model.method

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodDeclaration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST

class MethodModelFactoryTest extends Specification {

    private static final AST ast = newAST(JLS8)
    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })

    @Subject private MethodModelFactory methodModelFactory = new MethodModelFactory(nodeFactory)

    def 'should return TestMethodModel for test method'() {
        expect:
        methodModelFactory.get(methodAnnotatedWith('Test')) instanceof TestMethodModel
    }

    @Unroll
    def 'should return FixtureMethodModel for fixture method (#methodName)'() {
        when:
        MethodModel methodModel = methodModelFactory.get(methodAnnotatedWith(annotation))

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

    def 'should return RegularMethodModel for regular method'() {
        expect:
        methodModelFactory.get(methodDeclaration) instanceof RegularMethodModel

        where:
        methodDeclaration << [methodAnnotatedWith('SomOtherAnnotation'), aMethod(ast).build()]
    }

    private static MethodDeclaration methodAnnotatedWith(String annotationName) {
        def testAnnotation = ast.newMarkerAnnotation()
        testAnnotation.setTypeName(ast.newName(annotationName))
        aMethod(ast).withAnnotation(testAnnotation).build()
    }
}
