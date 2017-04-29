package com.github.opaluchlukasz.junit2spock.core.model

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import org.eclipse.jdt.core.dom.AST
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST

class ImportFilterTest extends Specification {

    private static final AST ast = newAST(JLS8)

    @Shared private ASTNodeFactory nodeFactory = new ASTNodeFactory({
        get: ast
    })
    @Subject private ImportFilter importFilter = new ImportFilter()

    def 'should not filter out static imports'() {
        expect:
        importFilter.test(nodeFactory.importDeclaration(imported, true))

        where:
        imported << ['foo.SomeClass.method', 'java.io', 'java.lang', 'java.math.BigDecimal',
                     'java.math.BigInteger', 'java.net', 'java.util']
    }

    def 'should not filter out non-default Groovy imports'() {
        expect:
        importFilter.test(nodeFactory.importDeclaration(imported, true))

        where:
        imported << ['foo.SomeClass', 'java.time.ZonedDateTime', 'org.omg.CORBA']
    }

    def 'should filter out default Groovy imports'() {
        expect:
        !importFilter.test(nodeFactory.importDeclaration(imported, false))

        where:
        imported << ['java.io.File', 'java.lang.String', 'java.math.BigDecimal',
                     'java.math.BigInteger', 'java.net.URL', 'java.util.function.Predicate']
    }
}
