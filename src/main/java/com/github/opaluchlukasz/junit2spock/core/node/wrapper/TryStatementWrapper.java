package com.github.opaluchlukasz.junit2spock.core.node.wrapper;

import com.github.opaluchlukasz.junit2spock.core.Applicable;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TryStatement;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.SEPARATOR;
import static com.github.opaluchlukasz.junit2spock.core.util.StringUtil.indentation;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;

public class TryStatementWrapper extends BaseWrapper {

    private final LinkedList body = new LinkedList();
    private final Optional<List<Object>> finallyBody;
    private final LinkedList<CatchClauseWrapper> catchClauses;

    TryStatementWrapper(TryStatement statement, int indentationLevel, Applicable applicable) {
        super(indentationLevel, applicable);
        body.addAll(statement.resources());
        ofNullable(statement.getBody()).ifPresent(block -> body.addAll(block.statements()));
        this.catchClauses = (LinkedList<CatchClauseWrapper>) statement.catchClauses().stream()
                .map(catchClause -> new CatchClauseWrapper((CatchClause) catchClause, indentationLevel, applicable))
                .collect(toCollection(LinkedList::new));
        finallyBody = ofNullable(statement.getFinally())
                .map(Block::statements);

        applicable.applyFeaturesToStatements(body);
        finallyBody.ifPresent(applicable::applyFeaturesToStatements);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("try {").append(SEPARATOR);
        body.forEach(printStatement(stringBuilder));
        stringBuilder.append(indentation(indentationLevel() + 1)).append("}");
        catchClauses.forEach(printStatement(stringBuilder));
        finallyBody.ifPresent(stmts -> {
            stringBuilder.append(" finally {").append(SEPARATOR);
            stmts.forEach(printStatement(stringBuilder));
            stringBuilder.append(indentation(indentationLevel() + 1)).append("}");
        });

        return stringBuilder.append(SEPARATOR).toString();
    }

    private static class CatchClauseWrapper extends BaseWrapper {
        private final LinkedList body = new LinkedList();
        private final SingleVariableDeclaration exception;

        CatchClauseWrapper(CatchClause catchClause, int indentationLevel, Applicable applicable) {
            super(indentationLevel, applicable);
            ofNullable(catchClause.getBody()).ifPresent(block -> body.addAll(block.statements()));
            exception = catchClause.getException();
            applicable.applyFeaturesToStatements(body);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" catch(").append(exception).append(") {").append(SEPARATOR);
            body.forEach(printStatement(stringBuilder));

            return stringBuilder.append(indentation(indentationLevel() + 1)).append("}").toString();
        }
    }
}
