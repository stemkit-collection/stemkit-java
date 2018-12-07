package com.bystr.sk.util;

@FunctionalInterface
public interface ExceptionalConsumer<T> {
    void accept(final T object) throws Exception;
}
