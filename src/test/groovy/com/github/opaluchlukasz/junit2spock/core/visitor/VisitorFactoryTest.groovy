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
        Supplier<TestClassVisitor> testClassVisitorSupplier = visitorFactory.testClassVisitor()

        when:
        TestClassVisitor visitor1 = testClassVisitorSupplier.get()
        TestClassVisitor visitor2 = testClassVisitorSupplier.get()

        then:
        visitor1 != visitor2
    }

    def 'should return new instance of InterfaceVisitor on every invocation'() {
        given:
        Supplier<InterfaceVisitor> interfaceVisitorSupplier = visitorFactory.interfaceVisitor()

        when:
        InterfaceVisitor visitor1 = interfaceVisitorSupplier.get()
        InterfaceVisitor visitor2 = interfaceVisitorSupplier.get()

        then:
        visitor1 != visitor2
    }

    def 'should return new instance of MethodVisitor on every invocation'() {
        given:
        Supplier<MethodVisitor> methodVisitor = visitorFactory.methodVisitor()

        when:
        MethodVisitor visitor1 = methodVisitor.get()
        MethodVisitor visitor2 = methodVisitor.get()

        then:
        visitor1 != visitor2
    }
}
