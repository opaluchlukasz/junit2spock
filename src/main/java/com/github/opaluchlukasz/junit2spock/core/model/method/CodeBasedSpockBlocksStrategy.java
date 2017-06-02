package com.github.opaluchlukasz.junit2spock.core.model.method;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;

import java.util.List;

import static com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertEqualsFeature.ASSERT_ARRAY_EQUALS;
import static com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertEqualsFeature.ASSERT_EQUALS;
import static com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertFalseFeature.ASSERT_FALSE;
import static com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertNotNullFeature.ASSERT_NOT_NULL;
import static com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertNullFeature.ASSERT_NULL;
import static com.github.opaluchlukasz.junit2spock.core.feature.junit.AssertTrueFeature.ASSERT_TRUE;
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.GivenWillReturnFeature.WILL_RETURN;
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.GivenWillThrowFeature.WILL_THROW;
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.MockitoVerifyFeature.VERIFY;
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.MockitoVerifyNoMoreInteractionsFeature.VERIFY_NO_MORE_INTERACTIONS;
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.WhenThenReturnFeature.THEN_RETURN;
import static com.github.opaluchlukasz.junit2spock.core.feature.mockito.WhenThenThrowFeature.THEN_THROW;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.expect;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.given;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.then;
import static com.github.opaluchlukasz.junit2spock.core.node.SpockBlockNode.when;
import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;
import static org.spockframework.util.Identifiers.THROWN;

public class CodeBasedSpockBlocksStrategy implements SpockBlockApplier {

    private static final String ASSERT_THAT = "assertThat";

    public static final String[] THEN_BLOCK_START = new String[]{ASSERT_EQUALS, ASSERT_NOT_NULL, ASSERT_ARRAY_EQUALS, ASSERT_TRUE,
        ASSERT_FALSE, ASSERT_NULL, THROWN, ASSERT_THAT, VERIFY, VERIFY_NO_MORE_INTERACTIONS};
    private static final String[] MOCKING  = {THEN_RETURN, THEN_THROW, WILL_RETURN, WILL_THROW};

    private final List<Object> body;

    CodeBasedSpockBlocksStrategy(List<Object> body) {
        this.body = body;
    }

    @Override
    public boolean apply() {
        int thenIndex = thenExpectBlockStart();
        int whenIndex = whenBlockStart(thenIndex);
        int givenIndex = givenBlockStart(whenIndex, thenIndex);

        if (whenIndex != -1) {
            body.add(thenIndex, then());
            body.add(whenIndex, when());
        } else {
            body.add(thenIndex, expect());
        }

        if (givenIndex != -1) {
            body.add(givenIndex, given());
        }
        return true;
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

        if (body.get(thenIndex - 1) instanceof ExpressionStatement) {
            Expression expression = ((ExpressionStatement) body.get(thenIndex - 1)).getExpression();
            if (expression instanceof MethodInvocation) {
                return !methodInvocation(body.get(thenIndex - 1), MOCKING).isPresent() ? (thenIndex - 1) : -1;
            }
        }

        return -1;
    }

    private int thenExpectBlockStart() {
        for (int i = 0; i < body.size(); i++) {
            if (methodInvocation(body.get(i), THEN_BLOCK_START).isPresent()) {
                return i;
            }
        }
        return 0;
    }
}
