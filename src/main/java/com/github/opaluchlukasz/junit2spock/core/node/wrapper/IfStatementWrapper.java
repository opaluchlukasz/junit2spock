package com.github.opaluchlukasz.junit2spock.core.node.wrapper;

import com.github.opaluchlukasz.junit2spock.core.Applicable;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;

import java.util.LinkedList;

import static com.github.opaluchlukasz.junit2spock.core.node.wrapper.WrapperDecorator.wrap;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.indentation;
import static java.util.regex.Pattern.quote;

public class IfStatementWrapper extends BaseWrapper {

    private final Expression expression;
    private final LinkedList thenBlock;
    private final LinkedList elseBlock;

    public IfStatementWrapper(IfStatement statement, int indentationLevel, Applicable applicable) {
        super(indentationLevel, applicable);
        this.expression = statement.getExpression();
        this.thenBlock = new LinkedList<>();
        this.elseBlock = new LinkedList<>();

        statementsFrom(statement.getThenStatement(), thenBlock);
        statementsFrom(statement.getElseStatement(), elseBlock);
        applicable.applyFeaturesToStatements(thenBlock);
        applicable.applyFeaturesToStatements(elseBlock);
    }

    private void statementsFrom(Statement statement, LinkedList extracted) {
        if (statement instanceof Block) {
            ((Block) statement).statements().forEach(stmt -> extracted.add(wrap(stmt, indentationLevel() + 1, applicable())));
        } else if (statement != null) {
            extracted.add(wrap(statement, indentationLevel(), applicable()));
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(indentation(indentationLevel() + 1)).append("if (").append(expression.toString()).append(") {").append(SEPARATOR);
        thenBlock.forEach(printStatement(stringBuilder));
        stringBuilder.append(indentation(indentationLevel() + 1)).append("}");
        if (elseBlock.size() == 1 && elseBlock.get(0) instanceof IfStatementWrapper) {
            stringBuilder.append(" else ").append(elseBlock.get(0).toString().replaceFirst(quote("\t"), ""));
        } else if (!elseBlock.isEmpty()) {
            stringBuilder.append(" else {").append(SEPARATOR);
            elseBlock.forEach(printStatement(stringBuilder));
            stringBuilder.append(indentation(indentationLevel() + 1)).append("}");
        }
        return stringBuilder.append(SEPARATOR).toString();
    }
}
