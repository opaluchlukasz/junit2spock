package com.github.opaluchlukasz.junit2spock.core.node;

import com.github.opaluchlukasz.junit2spock.core.ASTNodeFactory;
import com.github.opaluchlukasz.junit2spock.core.Applicable;
import com.github.opaluchlukasz.junit2spock.core.groovism.Groovism;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;

import java.util.LinkedList;
import java.util.function.Consumer;

import static com.github.opaluchlukasz.junit2spock.core.groovism.GroovismChainProvider.provide;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.indentation;

public class IfStatementWrapper {

    private final Expression expression;
    private final LinkedList thenBlock;
    private final LinkedList elseBlock;
    private final int indentationLevel;
    private final Applicable applicable;
    private final ASTNodeFactory astNodeFactory;
    private final Groovism groovism;

    public IfStatementWrapper(IfStatement statement, int indentationLevel, Applicable applicable) {
        this.astNodeFactory = new ASTNodeFactory(statement.getAST());
        this.expression = statement.getExpression();
        this.indentationLevel = indentationLevel;
        this.applicable = applicable;
        this.groovism = provide();
        this.thenBlock = new LinkedList<>();
        this.elseBlock = new LinkedList<>();
        statementsFrom(statement.getThenStatement(), thenBlock);
        statementsFrom(statement.getElseStatement(), elseBlock);
        applicable.applyFeaturesToStatements(thenBlock, astNodeFactory);
        applicable.applyFeaturesToStatements(elseBlock, astNodeFactory);
    }

    private void statementsFrom(Statement statement, LinkedList extracted) {
        if (statement instanceof Block) {
            ((Block) statement).statements().forEach(stmt -> extracted.add(wrap(stmt, indentationLevel + 1)));
        } else if (statement != null) {
            extracted.add(wrap(statement, indentationLevel));
        }
    }

    private Object wrap(Object statement, int indentation) {
        if (statement instanceof IfStatement) {
            return new IfStatementWrapper((IfStatement) statement, indentation, applicable);
        }
        return statement;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(indentation(indentationLevel + 1)).append("if (").append(expression.toString()).append(") {").append(SEPARATOR);
        thenBlock.forEach(printStatement(stringBuilder));
        stringBuilder.append(indentation(indentationLevel + 1)).append("}");
        if (elseBlock.size() == 1 && elseBlock.get(0) instanceof IfStatementWrapper) {
            stringBuilder.append(" else ");
            elseBlock.forEach(printStatement(stringBuilder));
        } else if (!elseBlock.isEmpty()) {
            stringBuilder.append(" else {").append(SEPARATOR);
            elseBlock.forEach(printStatement(stringBuilder));
            stringBuilder.append(indentation(indentationLevel + 1)).append("}");
        }
        return stringBuilder.append(SEPARATOR).toString();
    }

    private Consumer printStatement(StringBuilder builder) {
        return stmt -> {
            if (stmt instanceof IfStatementWrapper) {
                builder.append(stmt.toString());
            } else {
                builder.append(indentation(indentationLevel + 2)).append(groovism.apply(stmt.toString()));
                if (!stmt.toString().endsWith("\n")) {
                    builder.append(SEPARATOR);
                }
            }
        };
    }
}
