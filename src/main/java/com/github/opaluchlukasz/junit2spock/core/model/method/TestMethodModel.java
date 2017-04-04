package com.github.opaluchlukasz.junit2spock.core.model.method;

import com.github.opaluchlukasz.junit2spock.core.Applicable;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.TypeLiteral;

import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.Applicable.TEST_METHOD;
import static com.github.opaluchlukasz.junit2spock.core.feature.AssertEqualsFeature.ASSERT_ARRAY_EQUALS;
import static com.github.opaluchlukasz.junit2spock.core.feature.AssertEqualsFeature.ASSERT_EQUALS;
import static com.github.opaluchlukasz.junit2spock.core.feature.AssertFalseFeature.ASSERT_FALSE;
import static com.github.opaluchlukasz.junit2spock.core.feature.AssertNotNullFeature.ASSERT_NOT_NULL;
import static com.github.opaluchlukasz.junit2spock.core.feature.AssertNullFeature.ASSERT_NULL;
import static com.github.opaluchlukasz.junit2spock.core.feature.AssertTrueFeature.ASSERT_TRUE;
import static com.github.opaluchlukasz.junit2spock.core.feature.GivenWillReturnFeature.WILL_RETURN;
import static com.github.opaluchlukasz.junit2spock.core.feature.MockitoVerifyFeature.VERIFY;
import static com.github.opaluchlukasz.junit2spock.core.feature.MockitoVerifyNoMoreInteractionsFeature.VERIFY_NO_MORE_INTERACTIONS;
import static com.github.opaluchlukasz.junit2spock.core.feature.WhenThenReturnFeature.THEN_RETURN;
import static com.github.opaluchlukasz.junit2spock.core.feature.WhenThenThrowFeature.THEN_THROW;
import static com.github.opaluchlukasz.junit2spock.core.model.method.MethodDeclarationHelper.annotatedWith;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.expect;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.given;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.then;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.when;
import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;
import static org.apache.commons.lang3.StringUtils.wrapIfMissing;

public class TestMethodModel extends MethodModel {

    private static final String THROWN = "thrown";
    private static final String ASSERT_THAT = "assertThat";

    public static final String[] THEN_BLOCK_START = new String[]{ASSERT_EQUALS, ASSERT_NOT_NULL, ASSERT_ARRAY_EQUALS, ASSERT_TRUE,
        ASSERT_FALSE, ASSERT_NULL, THROWN, ASSERT_THAT, VERIFY, VERIFY_NO_MORE_INTERACTIONS};
    private static final String[] MOCKING  = {THEN_RETURN, THEN_THROW, WILL_RETURN};

    TestMethodModel(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);

        addThrownSupport(methodDeclaration);
        addSpockSpecificBlocksToBody();
        methodType().applyFeaturesToStatements(body(), astNodeFactory());
    }

    @Override
    protected String methodSuffix() {
        return SEPARATOR;
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

    @Override
    protected Optional<String> methodModifier() {
        return Optional.of("def");
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

    private void addSpockSpecificBlocksToBody() {
        int thenIndex = thenExpectBlockStart();
        boolean then = isWhenThenStrategy(thenIndex);

        List<Object> body = body();

        if (then) {
            body.add(thenIndex, then());
            body.add(thenIndex - 1, when());
            if (thenIndex - 2 >= 0) {
                body.add(0, given());
            }
        } else {
            body.add(thenIndex, expect());
            if (thenIndex - 1 >= 0) {
                body.add(0, given());
            }
        }
    }

    private boolean isWhenThenStrategy(int index) {
        if (index == 0) {
            return false;
        } else {
            List<Object> body = body();
            if (body.get(index - 1) instanceof ExpressionStatement) {
                Expression expression = ((ExpressionStatement) body.get(index - 1)).getExpression();
                if (expression instanceof MethodInvocation) {
                    return !methodInvocation(body.get(index - 1), MOCKING).isPresent();
                }
            }
        }
        return false;
    }

    private int thenExpectBlockStart() {
        List<Object> body = body();

        for (int i = 0; i < body.size(); i++) {
            if (methodInvocation(body.get(i), THEN_BLOCK_START).isPresent()) {
                return i;
            }
        }
        return 0;
    }
}
