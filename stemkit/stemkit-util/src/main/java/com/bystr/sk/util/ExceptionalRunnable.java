package com.bystr.sk.util;

@FunctionalInterface
public interface ExceptionalRunnable {
    void run() throws Exception;
}
