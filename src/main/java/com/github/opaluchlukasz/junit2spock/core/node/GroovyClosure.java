package com.github.opaluchlukasz.junit2spock.core.node;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.groovism.Groovism;
import com.github.opaluchlukasz.junit2spock.core.node.wrapper.IfStatementWrapper;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.github.opaluchlukasz.junit2spock.core.Applicable.REGULAR_METHOD;
import static com.github.opaluchlukasz.junit2spock.core.groovism.GroovismChainProvider.provide;
import static com.github.opaluchlukasz.junit2spock.core.node.wrapper.WrapperDecorator.wrap;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public class GroovyClosure {

    static final int NODE_TYPE = -13;
    private final SingleVariableDeclaration[] arguments;
    private final Groovism groovism;
    private final List<Object> body = new LinkedList<>();

    GroovyClosure(ASTNodeFactory nodeFactory, List<Statement> statements, SingleVariableDeclaration... arguments) {

        statements.forEach(statement -> this.body.add(wrap(statement, 2, REGULAR_METHOD)));

        if (body.size() > 0) {
            Object statement = body.get(body.size() - 1);
            if (statement instanceof ReturnStatement) {
                body.add(body.size(), nodeFactory.expressionStatement(nodeFactory
                        .clone(((ReturnStatement) statement).getExpression())));
                body.remove(statement);
            }
        }

        this.arguments = arguments;
        this.groovism = provide();
    }

    @Override
    public String toString() {
        return body.stream()
                .map(statement -> format(indent(statement) + "%s", groovism.apply(statement.toString())))
                .collect(joining(SEPARATOR, prefix(), "\t\t}"));
    }

    private String indent(Object statement) {
        if (statement instanceof IfStatementWrapper) {
            return "";
        }
        return "\t\t\t";
    }

    private String prefix() {
        return arguments.length == 0 ? "{"  + SEPARATOR : Arrays.stream(arguments)
                .map(Object::toString)
                .collect(joining(", ", "{ ", " ->" + SEPARATOR));
    }
}
