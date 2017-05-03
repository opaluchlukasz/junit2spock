package com.github.opaluchlukasz.junit2spock.core.visitor;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.model.method.MethodModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class VisitorFactory {

    @Autowired private MethodModelFactory methodModelFactory;
    @Autowired private ASTNodeFactory astNodeFactory;

    @Bean
    public Supplier<TypeVisitor> testClassVisitor() {
        return () -> new TypeVisitor(methodModelFactory, astNodeFactory);
    }
}
