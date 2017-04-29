package com.github.opaluchlukasz.junit2spock.core.model.method;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.Applicable;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import static com.github.opaluchlukasz.junit2spock.core.Applicable.REGULAR_METHOD;
import static java.util.stream.Collectors.joining;

public class RegularMethodModel extends MethodModel {

    RegularMethodModel(ASTNodeFactory nodeFactory, MethodDeclaration methodDeclaration) {
        super(nodeFactory, methodDeclaration);
        methodType().applyFeaturesToStatements(body(), astNodeFactory());
    }

    @Override
    protected String methodSuffix() {
        return "";
    }

    @Override
    protected String getMethodName() {
        return methodDeclaration().getName().toString();
    }

    @Override
    protected Applicable methodType() {
        return REGULAR_METHOD;
    }

    @Override
    protected String methodModifier() {
        if (methodDeclaration().modifiers().size() == 0) {
            return "";
        }
        return (String) methodDeclaration().modifiers().stream().map(Object::toString).collect(joining(" ", "", " "));
    }
}
