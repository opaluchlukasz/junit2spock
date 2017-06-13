package com.github.opaluchlukasz.junit2spock.core.node;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

//TODO rewrite as spring bean
public final class ClosureHelper {

    private ClosureHelper() {
        //NOOP
    }

    public static Optional<GroovyClosure> asClosure(ASTNodeFactory nodeFactory, GroovyClosureBuilder groovyClosureBuilder,
                                          Expression expression, String methodName) {
        if (expression instanceof ClassInstanceCreation) {
            ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
            if (classInstanceCreation.getAnonymousClassDeclaration() != null) {
                AnonymousClassDeclaration classDeclaration = classInstanceCreation.getAnonymousClassDeclaration();
                if (classDeclaration.bodyDeclarations().size() == 1 &&
                        classDeclaration.bodyDeclarations().get(0) instanceof MethodDeclaration &&
                        ((MethodDeclaration) classDeclaration.bodyDeclarations().get(0))
                                .getName().getIdentifier().equals(methodName)) {
                    MethodDeclaration methodDeclaration = (MethodDeclaration) classDeclaration.bodyDeclarations().get(0);
                    List<Statement> statements = nodeFactory.clone(methodDeclaration.getBody()).statements();
                    GroovyClosure closure = groovyClosureBuilder.aClosure()
                            .withBodyStatements(statements)
                            .withTypeLiteral(nodeFactory.typeLiteral(type(nodeFactory, classInstanceCreation)))
                            .withArgument(nodeFactory.clone((SingleVariableDeclaration) methodDeclaration.parameters().get(0)))
                            .build();
                    return Optional.of(closure);
                }
            }
        }
        return empty();
    }

    //TODO support for different type strategies - this one works for ArgumentMatcher and some other Mockito interfaces
    private static Type type(ASTNodeFactory nodeFactory, ClassInstanceCreation classInstanceCreation) {
        Type type = classInstanceCreation.getType();
        return type instanceof ParameterizedType ?
                nodeFactory.clone((Type) ((ParameterizedType) type).typeArguments().get(0)) : nodeFactory.simpleType(Object.class.getSimpleName());
    }
}
