package com.github.opaluchlukasz.junit2spock.core.model.method.feature;

public interface TestMethodFeature {
    boolean applicable(Object object);
    Object apply(Object object);
}
