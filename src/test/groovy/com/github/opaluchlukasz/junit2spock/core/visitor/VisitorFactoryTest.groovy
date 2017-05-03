package com.github.opaluchlukasz.junit2spock.core.visitor

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.AstProxy
import com.github.opaluchlukasz.junit2spock.core.model.method.MethodModelFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Subject

import java.util.function.Supplier

@ContextConfiguration(classes = [AstProxy, ASTNodeFactory, MethodModelFactory, VisitorFactory])
class VisitorFactoryTest extends Specification {

    @Autowired @Subject private VisitorFactory visitorFactory

    def 'should return new instance of TestClassVisitor on every invocation'() {
        given:
        Supplier<TypeVisitor> testClassVisitorSupplier = visitorFactory.testClassVisitor()

        when:
        TypeVisitor visitor1 = testClassVisitorSupplier.get()
        TypeVisitor visitor2 = testClassVisitorSupplier.get()

        then:
        visitor1 != visitor2
    }
}
