package com.github.opaluchlukasz.junit2spock.core.node

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class SpockBlockNodeTest extends Specification {

    def 'equals should be properly defined'() {
        expect:
        EqualsVerifier.forClass(SpockBlockNode.class).verify()
    }
}
