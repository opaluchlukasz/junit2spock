package com.github.opaluchlukasz.junit2spock.core.model.method;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.feature.Feature;
import com.github.opaluchlukasz.junit2spock.core.feature.FeatureProvider;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.TypeLiteral;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.model.method.MethodDeclarationHelper.annotatedWith;
import static com.github.opaluchlukasz.junit2spock.core.feature.AssertEqualsFeature.ASSERT_ARRAY_EQUALS;
import static com.github.opaluchlukasz.junit2spock.core.feature.AssertEqualsFeature.ASSERT_EQUALS;
import static com.github.opaluchlukasz.junit2spock.core.feature.AssertFalseFeature.ASSERT_FALSE;
import static com.github.opaluchlukasz.junit2spock.core.feature.AssertNotNullFeature.ASSERT_NOT_NULL;
import static com.github.opaluchlukasz.junit2spock.core.feature.AssertNullFeature.ASSERT_NULL;
import static com.github.opaluchlukasz.junit2spock.core.feature.AssertTrueFeature.ASSERT_TRUE;
import static com.github.opaluchlukasz.junit2spock.core.feature.ThenReturnFeature.THEN_RETURN;
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
    public static final String[] THEN_BLOCK_START = {ASSERT_EQUALS, ASSERT_NOT_NULL, ASSERT_ARRAY_EQUALS, ASSERT_TRUE,
        ASSERT_FALSE, ASSERT_NULL, THROWN};
    private static final String[] MOCKING  = {THEN_RETURN};
    private final List<Object> body = new LinkedList<>();

    private final ASTNodeFactory astNodeFactory;

    TestMethodModel(MethodDeclaration methodDeclaration) {
        super(methodDeclaration);
        astNodeFactory = new ASTNodeFactory(methodDeclaration.getAST());
        if (methodDeclaration.getBody() != null && methodDeclaration.getBody().statements() != null) {
            body.addAll(methodDeclaration.getBody().statements());
        }

        addThrownSupport(methodDeclaration);
        addSpockSpecificBlocksToBody();
        applyTestMethodFeatures();
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

        expected.ifPresent(expression -> body
                .add(astNodeFactory.methodInvocation(THROWN,
                        singletonList(astNodeFactory.simpleName(((TypeLiteral) expression).getType().toString())))));
    }

    private Optional<Expression> expectedException(Annotation annotation) {
        return ((NormalAnnotation) annotation).values().stream()
                .filter(value -> ((MemberValuePair) value).getName().getFullyQualifiedName().equals("expected"))
                .map(value -> ((MemberValuePair) value).getValue()).findFirst();
    }

    @Override
    protected List<Object> body() {
        return body;
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

    private void addSpockSpecificBlocksToBody() {
        int thenIndex = thenExpectBlockStart();
        boolean then = isWhenThenStrategy(thenIndex);

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

    private void applyTestMethodFeatures() {
        List<Feature> testMethodFeatures = new FeatureProvider(astNodeFactory).testMethodFeatures();
        for (int i = 0; i < body.size(); i++) {
            Object bodyNode = body.get(i);
            for (Feature testMethodFeature : testMethodFeatures) {
                if (testMethodFeature.applicable(bodyNode)) {
                    body.remove(bodyNode);
                    body.add(i, testMethodFeature.apply(bodyNode));
                }
            }
        }
    }

    private boolean isWhenThenStrategy(int index) {
        if (index == 0) {
            return false;
        } else {
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
        for (int i = 0; i < body.size(); i++) {
            if (methodInvocation(body.get(i), THEN_BLOCK_START).isPresent()) {
                return i;
            }
        }
        return 0;
    }
}
