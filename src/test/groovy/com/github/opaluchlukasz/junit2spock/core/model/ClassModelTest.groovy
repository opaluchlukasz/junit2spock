package com.github.opaluchlukasz.junit2spock.core.model

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.model.method.TestMethodModel
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.MethodDeclaration
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod

class ClassModelTest extends Specification {

    private ASTNodeFactory astNodeFactory = new ASTNodeFactory()

    def 'should extend spock.lang.Specification class when there are test methods in class'() {
        given:
        def testClassName = 'TestClass'
        AST ast = AST.newAST(AST.JLS8)
        MethodDeclaration methodDeclaration = aMethod(ast).build()

        when:
        def testClass = new ClassModelBuilder()
                .withClassName(astNodeFactory.simpleName(testClassName))
                .withMethod(new TestMethodModel(methodDeclaration))
                .build()

        then:
        testClass.asGroovyClass().contains("class $testClassName extends Specification")
    }

    def 'should not extend declared supertype class when there are no test methods in class'() {
        given:
        def testClassName = 'TestClass'

        when:
        def testClass = new ClassModelBuilder()
                .withClassName(astNodeFactory.simpleName(testClassName))
                .withSuperType(astNodeFactory.simpleType('Object'))
                .build()

        then:
        testClass.asGroovyClass().contains("class $testClassName extends Object")
    }


    def 'should add spock.lang.Specification import statement when there are test methods in class'() {
        given:
        AST ast = AST.newAST(AST.JLS8)
        MethodDeclaration methodDeclaration = aMethod(ast).build()

        when:
        def classModel = new ClassModelBuilder()
                .withMethod(new TestMethodModel(methodDeclaration))
                .build()

        then:
        classModel.imports.find {declaration -> "$declaration.importName" == "${Specification.getName()}"}
    }

    def 'should not add spock.lang.Specification import statement when there are no test methods in class'() {
        when:
        def classModel = new ClassModelBuilder().build()

        then:
        classModel.imports
                .findAll {declaration -> "$declaration.importName" == "${Specification.getName()}"} .size() == 0
    }
}
