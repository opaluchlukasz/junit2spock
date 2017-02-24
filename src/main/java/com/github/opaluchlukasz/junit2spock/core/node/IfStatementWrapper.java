package com.github.opaluchlukasz.junit2spock.core.node;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;

import java.util.LinkedList;

import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.indentation;

public class IfStatementWrapper {

    private final Expression expression;
    private final LinkedList thenBlock;
    private final LinkedList elseBlock;
    private final int indentationLevel;

    public IfStatementWrapper(IfStatement statement, int indentationLevel) {
        this.expression = statement.getExpression();
        this.indentationLevel = indentationLevel;
        this.thenBlock = new LinkedList<>();
        this.elseBlock = new LinkedList<>();
        //TODO recursive
        statementsFrom(statement.getThenStatement(), thenBlock);
        statementsFrom(statement.getElseStatement(), elseBlock);
    }

    private static void statementsFrom(Statement statement, LinkedList extracted) {
        if (statement instanceof Block) {
            extracted.addAll(((Block) statement).statements());
        } else if (statement != null) {
            extracted.add(statement);
        }
    }

    @Override
    public String toString() {
        // TODO else if
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(indentation(indentationLevel + 1)).append("if (").append(expression.toString()).append(") {").append(SEPARATOR);
        thenBlock.forEach(statement -> stringBuilder.append(indentation(indentationLevel + 2)).append(statement));
        if (!elseBlock.isEmpty()) {
            stringBuilder.append(indentation(indentationLevel + 1)).append("} else {").append(SEPARATOR);
            elseBlock.forEach(statement -> stringBuilder.append(indentation(indentationLevel + 2)).append(statement));
        }
        return stringBuilder.append(indentation(indentationLevel + 1)).append("}").append(SEPARATOR).toString();
    }
}
