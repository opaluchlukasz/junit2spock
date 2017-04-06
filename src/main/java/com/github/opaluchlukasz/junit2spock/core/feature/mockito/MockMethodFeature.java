package com.github.opaluchlukasz.junit2spock.core.feature.mockito;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.feature.Feature;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.util.AstNodeFinder.methodInvocation;
import static java.util.Collections.singletonList;

public class MockMethodFeature extends Feature<VariableDeclarationStatement> {

    private final ASTNodeFactory astNodeFactory;

    public MockMethodFeature(ASTNodeFactory astNodeFactory) {
        this.astNodeFactory = astNodeFactory;
    }

    @Override
    public Optional<VariableDeclarationStatement> applicable(Object astNode) {
        return Optional.of(astNode)
                .filter(node -> node instanceof VariableDeclarationStatement)
                .map(variableDeclarationStatement -> (VariableDeclarationStatement) variableDeclarationStatement)
                .filter(statement -> statement.fragments().size() > 0)
                .filter(statement -> statement.fragments().get(0) instanceof VariableDeclarationFragment)
                .filter(statement -> methodInvocation(((VariableDeclarationFragment) statement.fragments().get(0))
                        .getInitializer(), "mock").isPresent());
    }

    @Override
    public VariableDeclarationStatement apply(Object object, VariableDeclarationStatement variableDeclarationStatement) {
        variableDeclarationStatement.fragments().forEach(declarationFragment -> {
            Type clonedType = astNodeFactory.clone(variableDeclarationStatement.getType());
            ((VariableDeclarationFragment) declarationFragment).setInitializer(astNodeFactory
                    .methodInvocation("Mock", singletonList(astNodeFactory.typeLiteral(clonedType))));
        });
        return variableDeclarationStatement;
    }
}
