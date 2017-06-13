package com.github.opaluchlukasz.junit2spock.core.node

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory
import com.github.opaluchlukasz.junit2spock.core.AstProvider
import com.github.opaluchlukasz.junit2spock.core.builder.ClassInstanceCreationBuilder
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.ClassInstanceCreation
import spock.lang.Shared
import spock.lang.Specification

import static com.github.opaluchlukasz.junit2spock.core.builder.ClassInstanceCreationBuilder.aClassInstanceCreation
import static com.github.opaluchlukasz.junit2spock.core.builder.MethodDeclarationBuilder.aMethod
import static com.github.opaluchlukasz.junit2spock.core.node.ClosureHelper.asClosure
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR
import static org.eclipse.jdt.core.dom.AST.JLS8
import static org.eclipse.jdt.core.dom.AST.newAST
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.EQUALS

class ClosureHelperTest extends Specification {

    private static final AST ast = newAST(JLS8)
    private static final AstProvider AST_PROVIDER = {
        get: ast
    }
    @Shared private ASTNodeFactory nf = new ASTNodeFactory(AST_PROVIDER)
    @Shared private GroovyClosureBuilder groovyClosureBuilder = new GroovyClosureBuilder(AST_PROVIDER, nf)

    def 'should return closure instance for anonymous class instance creation with single member when method name matches'() {
        given:
        String methodName = 'someMethod'
        ClassInstanceCreation clazz = anonymousArgumentMatcherClass(Boolean, aMethod(ast)
                .withName(methodName)
                .withParameter(nf.singleVariableDeclaration(nf.simpleType(Boolean.simpleName), 'a'))
                .withBodyStatement(nf.returnStatement(nf.infixExpression(EQUALS, nf.simpleName('a'), nf.booleanLiteral(false))))
                .build())
        when:
        Optional<GroovyClosure> groovyClosure = asClosure(nf, groovyClosureBuilder, clazz, methodName)

        then:
        groovyClosure.isPresent()
        groovyClosure.get().asExpression().toString() == "{ Boolean a ->$SEPARATOR\t\t\ta == false\n\t\t} as Boolean.class"
    }

    def 'should not return closure instance when method name does not match'() {
        given:
        String methodName = 'someMethod'
        ClassInstanceCreation clazz = anonymousArgumentMatcherClass(Boolean, aMethod(ast)
                .withName(methodName)
                .withParameter(nf.singleVariableDeclaration(nf.simpleType(Boolean.simpleName), 'a'))
                .withBodyStatement(nf.returnStatement(nf.infixExpression(EQUALS, nf.simpleName('a'), nf.booleanLiteral(false))))
                .build())
        when:
        Optional<GroovyClosure> groovyClosure = asClosure(nf, groovyClosureBuilder, clazz, 'someOtherMethod')

        then:
        !groovyClosure.isPresent()
    }

    def 'should not return closure instance when anonymous class has multiple members'() {
        given:
        String methodName = 'someMethod'
        String methodName2 = 'someOtherMethod'
        ClassInstanceCreation clazz = anonymousArgumentMatcherClass(Boolean,
                aMethod(ast).withName(methodName).build(),
                aMethod(ast).withName(methodName2).build())

        when:
        Optional<GroovyClosure> groovyClosure = asClosure(nf, groovyClosureBuilder, clazz, methodName2)

        then:
        !groovyClosure.isPresent()
    }

    private ClassInstanceCreation anonymousArgumentMatcherClass(Class<?> clazz, ASTNode... bodyDeclarations) {
        ClassInstanceCreationBuilder builder = aClassInstanceCreation(ast)
                .withType(nf.parameterizedType(nf.simpleType('ArgumentMatcher'), [nf.simpleType(clazz.simpleName)]))
        bodyDeclarations.each {builder.withBodyDeclaration(it)}
        builder .build()
    }
}
