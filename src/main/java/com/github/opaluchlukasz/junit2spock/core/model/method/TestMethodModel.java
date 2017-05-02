package com.github.opaluchlukasz.junit2spock.core.model.method;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.Applicable;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.TypeLiteral;

import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.Applicable.TEST_METHOD;
import static com.github.opaluchlukasz.junit2spock.core.model.method.MethodDeclarationHelper.annotatedWith;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;
import static org.apache.commons.lang3.StringUtils.wrapIfMissing;
import static org.spockframework.util.Identifiers.THROWN;

public class TestMethodModel extends MethodModel {

    TestMethodModel(ASTNodeFactory astNodeFactory, MethodDeclaration methodDeclaration) {
        super(astNodeFactory, methodDeclaration);
        addThrownSupport(methodDeclaration());
        addSpockSpecificBlocksToBody();
        methodType().applyFeaturesToStatements(body());
    }

    @Override
    protected String methodSuffix() {
        return SEPARATOR;
    }

    @Override
    protected String methodModifier() {
        return "def ";
    }

    @Override
    protected String getMethodName() {
        return wrapIfMissing(join(splitByCharacterTypeCamelCase(methodDeclaration().getName().toString()), ' '), "'")
                .toLowerCase();
    }

    @Override
    protected Applicable methodType() {
        return TEST_METHOD;
    }

    private void addThrownSupport(MethodDeclaration methodDeclaration) {
        Optional<Annotation> testAnnotation = annotatedWith(methodDeclaration, "Test");
        Optional<Expression> expected = testAnnotation
                .filter(annotation -> annotation instanceof NormalAnnotation)
                .flatMap(this::expectedException);

        expected.ifPresent(expression -> body()
                .add(astNodeFactory().methodInvocation(THROWN,
                        singletonList(astNodeFactory().simpleName(((TypeLiteral) expression).getType().toString())))));
    }

    private Optional<Expression> expectedException(Annotation annotation) {
        return ((NormalAnnotation) annotation).values().stream()
                .filter(value -> ((MemberValuePair) value).getName().getFullyQualifiedName().equals("expected"))
                .map(value -> ((MemberValuePair) value).getValue()).findFirst();
    }

    private void addSpockSpecificBlocksToBody() {
        boolean applied = new CommentBasedSpockBlocksStrategy(body(), getMethodName()).apply();
        if (!applied) {
            new CodeBasedSpockBlocksStrategy(body()).apply();
        }
    }
}
