package com.github.opaluchlukasz.junit2spock.core.feature;

import java.util.Optional;

public abstract class Feature<T> {
    abstract Optional<T> applicable(Object object);
    abstract Object apply(Object object, T applicable);

    public final Object apply(Object astNode) {
        return applicable(astNode)
                .map(t -> apply(astNode, t))
                .orElse(astNode);
    }
}
