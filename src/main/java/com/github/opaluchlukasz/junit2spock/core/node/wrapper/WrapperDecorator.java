package com.github.opaluchlukasz.junit2spock.core.node.wrapper;

import com.github.opaluchlukasz.junit2spock.core.Applicable;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.TryStatement;

import static io.vavr.Predicates.instanceOf;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public final class WrapperDecorator {

    private WrapperDecorator() {
        // NOOP
    }

    public static Object wrap(Object statement, int indentation, Applicable applicable) {
        return Match(statement).of(
                Case($(instanceOf(IfStatement.class)), stmt -> new IfStatementWrapper(stmt, indentation, applicable)),
                Case($(instanceOf(TryStatement.class)), stmt -> new TryStatementWrapper(stmt, indentation, applicable)),
                Case($(), statement)
        );
    }
}
