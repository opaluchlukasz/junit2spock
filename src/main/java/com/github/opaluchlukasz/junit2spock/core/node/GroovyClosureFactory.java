package com.github.opaluchlukasz.junit2spock.core.node;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.AstProvider;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.internal.core.dom.NaiveASTFlattener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static com.github.opaluchlukasz.junit2spock.core.node.GroovyClosure.NODE_TYPE;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.makeAccessible;

@Component
public class GroovyClosureFactory {

    public static final String IT = "it";

    private final AstProvider astProvider;
    private final ASTNodeFactory astNodeFactory;

    @Autowired
    public GroovyClosureFactory(AstProvider astProvider, ASTNodeFactory astNodeFactory) {
        this.astProvider = astProvider;
        this.astNodeFactory = astNodeFactory;
    }

    public Expression create(List<Statement> statements, SingleVariableDeclaration... arguments) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Expression.class);
        enhancer.setCallback(new ClosureInvocationHandler(astNodeFactory, statements, arguments));
        return (Expression) enhancer.create(new Class[] {AST.class}, new Object[] {astProvider.get()});
    }

    private static final class ClosureInvocationHandler implements MethodInterceptor {

        private final GroovyClosure groovyClosure;

        ClosureInvocationHandler(ASTNodeFactory astNodeFactory,
                                 List<Statement> statements,
                                 SingleVariableDeclaration... arguments) {
            this.groovyClosure = new GroovyClosure(astNodeFactory, statements, arguments);
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
