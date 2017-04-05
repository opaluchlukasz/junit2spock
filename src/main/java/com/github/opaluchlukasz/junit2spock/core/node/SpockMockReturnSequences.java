package com.github.opaluchlukasz.junit2spock.core.node;

import org.eclipse.jdt.core.dom.Expression;

import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public final class SpockMockReturnSequences {

    private final Expression expression;
    private final List<Object> returnedSequence;

    public SpockMockReturnSequences(Expression expression, List<Object> returnedSequence) {
        this.expression = expression;
        this.returnedSequence = returnedSequence;
    }

    @Override
    public String toString() {
        return format("%s >>> %s", expression, returnedSequenceAsArray());
    }

    private Object returnedSequenceAsArray() {
        return returnedSequence.stream()
                .map(Object::toString)
                .collect(joining(", ", "[", "]"));
    }
}
