package com.bystr.sk.util;

@FunctionalInterface
public interface ExceptionalSupplier<T> {
    T get() throws Exception;
}
