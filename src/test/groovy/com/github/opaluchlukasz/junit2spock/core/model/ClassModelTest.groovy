package com.github.opaluchlukasz.junit2spock.core.model

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.model.method.TestMethodModel
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodDeclaration
import spock.lang.Shared
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod
import static java.io.File.separator
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST

class ClassModelTest extends Specification {

    private static final String CLASS_NAME = 'TestClass'
    private static final AST ast = newAST(JLS8)

    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })

    def 'should extend spock.lang.Specification class when there are test methods in class'() {
        given:
        MethodDeclaration methodDeclaration = aMethod(ast).build()

        when:
        ClassModel classModel = new ClassModelBuilder(nodeFactory)
                .withClassName(nodeFactory.simpleName(CLASS_NAME))
                .withMethod(new TestMethodModel(nodeFactory, methodDeclaration))
                .build()

        then:
        classModel.asGroovyClass(0).contains("class $CLASS_NAME extends Specification")
    }

    def 'should not extend declared supertype class when there are no test methods in class'() {
        given:
        def testClassName = 'TestClass'

        when:
        ClassModel classModel = new ClassModelBuilder(nodeFactory)
                .withClassName(nodeFactory.simpleName(testClassName))
                .withSuperType(nodeFactory.simpleType(nodeFactory.simpleName('Object')))
                .build()

        then:
        classModel.asGroovyClass(0).contains("class $testClassName extends Object")
    }


    def 'should add spock.lang.Specification import statement when there are test methods in class'() {
        given:
        MethodDeclaration methodDeclaration = aMethod(ast).build()

        when:
        def classModel = new ClassModelBuilder(nodeFactory)
                .withMethod(new TestMethodModel(nodeFactory, methodDeclaration))
                .build()

        then:
        classModel.imports.find {declaration -> "$declaration.importName" == "${Specification.getName()}"}
    }

    def 'should not add spock.lang.Specification import statement when there are no test methods in class'() {
        when:
        def classModel = new ClassModelBuilder(nodeFactory).build()

        then:
        classModel.imports
                .findAll {declaration -> "$declaration.importName" == "${Specification.getName()}"} .size() == 0
    }

    def 'should return output file path'() {
        when:
        TypeModel classModel = new ClassModelBuilder(nodeFactory)
                .withClassName(nodeFactory.simpleName(CLASS_NAME))
                .withPackageDeclaration(nodeFactory.packageDeclaration(packageName))
                .build()

        then:
        classModel.outputFilePath() == outputPath

        where:
        packageName                                                                        | outputPath
        nodeFactory.simpleName('foo')                                                      | "foo${separator}${CLASS_NAME}.groovy"
        ast.newQualifiedName(nodeFactory.simpleName('foo'), nodeFactory.simpleName('bar')) | "foo${separator}bar${separator}${CLASS_NAME}.groovy"
    }
}
