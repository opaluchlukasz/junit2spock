package com.github.opaluchlukasz.junit2spock.core.model

import spock.lang.Specification

class ClassModelBuilderTest extends Specification {

    def 'should add spock.lang.Specification import statement'() {
        expect:
        new ClassModelBuilder().build().imports
                .find {declaration -> "$declaration.importName" == "${Specification.class.getName()}"}
    }
}
