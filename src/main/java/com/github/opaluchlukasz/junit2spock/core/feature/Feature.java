package com.github.opaluchlukasz.junit2spock.core.feature;

public interface Feature {
    boolean applicable(Object object);
    Object apply(Object object);
}
