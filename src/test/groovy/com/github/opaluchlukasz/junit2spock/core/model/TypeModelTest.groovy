package com.github.opaluchlukasz.junit2spock.core.model

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.AstProvider
import com.github.opaluchlukasz.junit2spock.core.model.method.TestMethodModel
import com.github.opaluchlukasz.junit2spock.core.util.TestConfig
import org.eclipse.jdt.core.dom.MethodDeclaration
import org.eclipse.jdt.core.dom.SimpleName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod
import static java.io.File.separator

@ContextConfiguration(classes = TestConfig.class)
class TypeModelTest extends Specification {

    private static final String CLASS_NAME = 'TestClass'

    @Autowired private ASTNodeFactory nodeFactory
    @Autowired private AstProvider astProvider

    def 'should extend spock.lang.Specification class when there are test methods in class'() {
        given:
        MethodDeclaration methodDeclaration = aMethod(astProvider.get()).build()

        when:
        ClassModel classModel = new TypeModelBuilder(nodeFactory)
                .withTypeName(nodeFactory.simpleName(CLASS_NAME))
                .withMethod(new TestMethodModel(nodeFactory, methodDeclaration))
                .build()

        then:
        classModel.asGroovyClass(0).contains("class $CLASS_NAME extends Specification")
    }

    def 'should not extend declared supertype class when there are no test methods in class'() {
        given:
        def testClassName = 'TestClass'

        when:
        TypeModel typeModel = new TypeModelBuilder(nodeFactory)
                .withTypeName(nodeFactory.simpleName(testClassName))
                .withSuperType(nodeFactory.simpleType('Object'))
                .build()

        then:
        typeModel.asGroovyClass(0).contains("class $testClassName extends Object")
    }


    def 'should add spock.lang.Specification import statement when there are test methods in class'() {
        given:
        MethodDeclaration methodDeclaration = aMethod(astProvider.get()).build()

        when:
        def classModel = new TypeModelBuilder(nodeFactory)
                .withMethod(new TestMethodModel(nodeFactory, methodDeclaration))
                .build()

        then:
        classModel.imports.find {declaration -> "$declaration.importName" == "${Specification.getName()}"}
    }

    def 'should not add spock.lang.Specification import statement when there are no test methods in class'() {
        when:
        def classModel = new TypeModelBuilder(nodeFactory).build()

        then:
        classModel.imports
                .findAll {declaration -> "$declaration.importName" == "${Specification.getName()}"} .size() == 0
    }

    def 'should return output file path for type with simple package declaration'() {
        when:
        TypeModel classModel = new TypeModelBuilder(nodeFactory)
                .withTypeName(nodeFactory.simpleName(CLASS_NAME))
                .withPackageDeclaration(nodeFactory.packageDeclaration(simpleName('foo')))
                .build()

        then:
        classModel.outputFilePath() == "foo${separator}${CLASS_NAME}.groovy"
    }

    def 'should return output file path for type with complex package declaration'() {
        when:
        TypeModel classModel = new TypeModelBuilder(nodeFactory)
                .withTypeName(nodeFactory.simpleName(CLASS_NAME))
                .withPackageDeclaration(nodeFactory
                .packageDeclaration(astProvider.get().newQualifiedName(simpleName('foo'), simpleName('bar'))))
                .build()

        then:
        classModel.outputFilePath() == "foo${separator}bar${separator}${CLASS_NAME}.groovy"
    }

    def 'should return output file path for type with no package declared'() {
        when:
        TypeModel classModel = new TypeModelBuilder(nodeFactory)
                .withTypeName(nodeFactory.simpleName(CLASS_NAME))
                .build()

        then:
        classModel.outputFilePath() == "${CLASS_NAME}.groovy"
    }

    private SimpleName simpleName(String name) {
        nodeFactory.simpleName(name)
    }
}
