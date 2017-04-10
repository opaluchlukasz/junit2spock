package com.github.opaluchlukasz.junit2spock.core.model.method;

import com.github.opaluchlukasz.junit2spock.core.Applicable;
import com.github.opaluchlukasz.junit2spock.core.Spocker;
import com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.Applicable.TEST_METHOD;
import static com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertEqualsFeature.ASSERT_ARRAY_EQUALS;
import static com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertEqualsFeature.ASSERT_EQUALS;
import static com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertFalseFeature.ASSERT_FALSE;
import static com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertNotNullFeature.ASSERT_NOT_NULL;
import static com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertNullFeature.ASSERT_NULL;
import static com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertTrueFeature.ASSERT_TRUE;
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.GivenWillReturnFeature.WILL_RETURN;
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.MockitoVerifyFeature.VERIFY;
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.MockitoVerifyNoMoreInteractionsFeature.VERIFY_NO_MORE_INTERACTIONS;
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.WhenThenReturnFeature.THEN_RETURN;
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.WhenThenThrowFeature.THEN_THROW;
import static com.github.opaluchlukasz.junit2spock.core.model.method.MethodDeclarationHelper.annotatedWith;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.expect;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.given;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.then;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.when;
import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static java.lang.String.format;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;
import static org.apache.commons.lang3.StringUtils.wrapIfMissing;

public class TestMethodModel extends MethodModel {

    private static final Logger LOG = LoggerFactory.getLogger(Spocker.class);

    private static final String THROWN = "thrown";
    private static final String ASSERT_THAT = "assertThat";

    public static final String[] THEN_BLOCK_START = new String[]{ASSERT_EQUALS, ASSERT_NOT_NULL, ASSERT_ARRAY_EQUALS, ASSERT_TRUE,
        ASSERT_FALSE, ASSERT_NULL, THROWN, ASSERT_THAT, VERIFY, VERIFY_NO_MORE_INTERACTIONS};
    private static final String[] MOCKING  = {THEN_RETURN, THEN_THROW, WILL_RETURN};
    public static final String GIVEN_BLOCK_START_MARKER_METHOD = "givenBlockStart";
    public static final String WHEN_BLOCK_START_MARKER_METHOD = "whenBlockStart";
    public static final String THEN_BLOCK_START_MARKER_METHOD = "thenBlockStart";

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
        List<Integer> givenIndexes = markerMethod(GIVEN_BLOCK_START_MARKER_METHOD);
        List<Integer> whenIndexes = markerMethod(WHEN_BLOCK_START_MARKER_METHOD);
        List<Integer> thenIndexes = markerMethod(THEN_BLOCK_START_MARKER_METHOD);

        Map<Integer, SpockBlockNode> blocksToBeAdded = new HashMap<>();
        if (givenIndexes.size() > 0) {
            blocksToBeAdded.put(givenIndexes.get(0), given());
        }

        if (whenIndexes.size() != thenIndexes.size()) {
            LOG.warn(format("Numbers of when/then blocks do not match for test method: %s", getMethodName()));
        }

        whenIndexes.forEach(index -> blocksToBeAdded.put(index, when()));
        thenIndexes.forEach(index -> blocksToBeAdded.put(index, then()));

        blocksToBeAdded.keySet().stream().sorted(reverseOrder()).forEach(key -> body().add(key, blocksToBeAdded.get(key)));

        body().removeIf(node -> methodInvocation(node, new String[]{GIVEN_BLOCK_START_MARKER_METHOD,
            WHEN_BLOCK_START_MARKER_METHOD, THEN_BLOCK_START_MARKER_METHOD}).isPresent());

        if (thenIndexes.size() == 0) {
            int thenIndex = thenExpectBlockStart();
            int whenIndex = whenBlockStart(thenIndex);
            int givenIndex = givenBlockStart(whenIndex, thenIndex);

            List<Object> body = body();

            if (whenIndex != -1) {
                body.add(thenIndex, then());
                body.add(whenIndex, when());
            } else {
                body.add(thenIndex, expect());
            }

            if (givenIndex != -1) {
                body.add(givenIndex, given());
            }
        }
    }

    private int givenBlockStart(int whenIndex, int thenIndex) {
        if (whenIndex == 0 || thenIndex == 0) {
            return -1;
        }
        return 0;
    }

    private int whenBlockStart(int thenIndex) {
        if (thenIndex == 0) {
            return -1;
        }

        List<Object> body = body();
        if (body.get(thenIndex - 1) instanceof ExpressionStatement) {
            Expression expression = ((ExpressionStatement) body.get(thenIndex - 1)).getExpression();
            if (expression instanceof MethodInvocation) {
                return !methodInvocation(body.get(thenIndex - 1), MOCKING).isPresent() ? thenIndex - 1 : -1;
            }
        }

        return -1;
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

    private List<Integer> markerMethod(String markerMethodName) {
        List<Integer> indexes = new LinkedList<>();
        List<Object> body = body();
        for (int i = 0; i < body.size(); i++) {
            if (methodInvocation(body.get(i), markerMethodName).isPresent()) {
                indexes.add(i);
            }
        }
        return indexes;
    }
}
