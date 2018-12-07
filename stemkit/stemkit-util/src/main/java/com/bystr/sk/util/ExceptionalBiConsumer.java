package com.bystr.sk.util;

@FunctionalInterface
public interface ExceptionalBiConsumer<T, U> {
    void accept(final T first, final U second) throws Exception;
}
