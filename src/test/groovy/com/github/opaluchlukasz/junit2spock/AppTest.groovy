package com.github.opaluchlukasz.junit2spock

import spock.lang.Specification

class AppTest extends Specification {

    def 'should throw exception if invoked with incorrect argument list'(String... args) {
        when:
        App.main(args)

        then:
        IllegalArgumentException ex = thrown()
        ex.message == "Source and output directory should be passed as arguments"

        where:
        args << [[], ['path1'], ['path1', 'path2', 'path3']]
    }
}
