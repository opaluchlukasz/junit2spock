package com.github.opaluchlukasz.junit2spock.core.model

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import spock.lang.Specification

class ClassModelBuilderTest extends Specification {

    private ASTNodeFactory astNodeFactory = new ASTNodeFactory()

    def 'should add spock.lang.Specification import statement'() {
        expect:
        new ClassModelBuilder().build().contains("import ${Specification.class.getName()}")
    }

    def 'should extend spock.lang.Specification class'() {
        given:
        def testClassName = 'TestClass'

        when:
        def testClass = new ClassModelBuilder().withClassName(astNodeFactory.simpleName(testClassName)).build()

        then:
        testClass.contains("class $testClassName extends Specification")
    }
}
