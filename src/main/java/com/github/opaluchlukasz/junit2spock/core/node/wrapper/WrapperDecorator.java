package com.github.opaluchlukasz.junit2spock.core.node.wrapper;

import com.github.opaluchlukasz.junit2spock.core.Applicable;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.TryStatement;

public final class WrapperDecorator {

    private WrapperDecorator() {
        // NOOP
    }

    public static Object wrap(Object statement, int indentation, Applicable applicable) {
        if (statement instanceof IfStatement) {
            return new IfStatementWrapper((IfStatement) statement, indentation, applicable);
        } else if (statement instanceof TryStatement) {
            return new TryStatementWrapper((TryStatement) statement, indentation, applicable);
        }
        return statement;
    }
}
