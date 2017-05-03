package com.github.opaluchlukasz.junit2spock.core.model.method

import com.github.opaluchlukasz.junit2spock.core.util.TestConfig
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MarkerAnnotation
import org.eclipse.jdt.core.dom.MethodDeclaration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST

@ContextConfiguration(classes = TestConfig.class)
class MethodModelFactoryTest extends Specification {

    @Autowired private MethodModelFactory methodModelFactory

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
        methodDeclaration << [methodAnnotatedWith('SomOtherAnnotation'), aMethod(newAST(JLS8)).build()]
    }

    private static MethodDeclaration methodAnnotatedWith(String annotationName) {
        AST ast = newAST(JLS8)
        MarkerAnnotation testAnnotation = ast.newMarkerAnnotation()
        testAnnotation.setTypeName(ast.newName(annotationName))
        aMethod(ast).withAnnotation(testAnnotation).build()
    }
}
