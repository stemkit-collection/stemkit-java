package com.bystr.sk.util;

@FunctionalInterface
public interface ExceptionalFunction<T, R> {
    R apply(final T object) throws Exception;
}
