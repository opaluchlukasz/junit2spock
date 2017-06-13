package com.github.opaluchlukasz.junit2spock.core.node;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.AstProvider;
import com.github.opaluchlukasz.junit2spock.core.groovism.Groovism;
import com.github.opaluchlukasz.junit2spock.core.node.wrapper.BaseWrapper;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.internal.core.dom.NaiveASTFlattener;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.Applicable.REGULAR_METHOD;
import static com.github.opaluchlukasz.junit2spock.core.groovism.GroovismChainProvider.provide;
import static com.github.opaluchlukasz.junit2spock.core.node.wrapper.WrapperDecorator.wrap;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.makeAccessible;

public class GroovyClosure {

    static final int NODE_TYPE = -13;
    private final List<SingleVariableDeclaration> arguments;
    private final Groovism groovism;
    private final AstProvider astProvider;
    private final List<Object> body = new LinkedList<>();
    private final Optional<TypeLiteral> typeLiteral;

    GroovyClosure(ASTNodeFactory nodeFactory, AstProvider astProvider, List<Statement> statements,
                  TypeLiteral typeLiteral, List<SingleVariableDeclaration> arguments) {
        statements.forEach(statement -> this.body.add(wrap(statement, 2, REGULAR_METHOD)));
        if (body.size() > 0) {
            Object statement = body.get(body.size() - 1);
            if (statement instanceof ReturnStatement) {
                body.add(body.size(), nodeFactory.expressionStatement(nodeFactory
                        .clone(((ReturnStatement) statement).getExpression())));
                body.remove(statement);
            }
        }

        this.typeLiteral = Optional.ofNullable(typeLiteral);
        this.arguments = arguments;
        this.groovism = provide();
        this.astProvider = astProvider;
    }

    @Override
    public String toString() {
        return body.stream()
                .map(statement -> format(indent(statement) + "%s", groovism.apply(statement.toString())))
                .collect(joining(SEPARATOR, prefix(), suffix()));
    }

    private String suffix() {
        return typeLiteral.map(literal -> format("\t\t} as %s", literal.toString())).orElse("\t\t}");
    }

    private String indent(Object statement) {
        if (statement instanceof BaseWrapper) {
            return "";
        }
        return "\t\t\t";
    }

    private String prefix() {
        return arguments.isEmpty() ? "{"  + SEPARATOR : arguments.stream()
                .map(Object::toString)
                .collect(joining(", ", "{ ", " ->" + SEPARATOR));
    }

    public Expression asExpression() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Expression.class);
        enhancer.setCallback(new GroovyClosure.ClosureInvocationHandler(this));
        return (Expression) enhancer.create(new Class[] {AST.class}, new Object[] {astProvider.get()});
    }

    private static final class ClosureInvocationHandler implements MethodInterceptor {

        private final GroovyClosure groovyClosure;

        ClosureInvocationHandler(GroovyClosure groovyClosure) {
            this.groovyClosure = groovyClosure;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            switch (method.getName()) {
                case "appendDebugString":
                    return ((StringBuffer) args[0]).append(groovyClosure.toString());
                case "getNodeType0":
                    return NODE_TYPE;
                case "accept0":
                    ASTVisitor visitor = ((ASTVisitor) args[0]);
                    if (visitor instanceof NaiveASTFlattener) {
                        Field field = findField(NaiveASTFlattener.class, "buffer");
                        makeAccessible(field);
                        ((StringBuffer) field.get(visitor)).append(groovyClosure.toString());
                    } else {
                        throw new UnsupportedOperationException();
                    }
                    return null;
                default:
                    makeAccessible(method);
                    return methodProxy.invokeSuper(o, args);
            }
        }
    }
}
