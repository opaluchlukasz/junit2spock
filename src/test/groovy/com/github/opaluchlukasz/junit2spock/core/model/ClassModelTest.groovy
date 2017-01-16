package com.github.opaluchlukasz.junit2spock.core.model

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import spock.lang.Specification

class ClassModelTest extends Specification {

    private ASTNodeFactory astNodeFactory = new ASTNodeFactory()

    def 'should extend spock.lang.Specification class'() {
        given:
        def testClassName = 'TestClass'

        when:
        def testClass = new ClassModelBuilder().withClassName(astNodeFactory.simpleName(testClassName)).build()

        then:
        testClass.asGroovyClass().contains("class $testClassName extends Specification")
    }
}
