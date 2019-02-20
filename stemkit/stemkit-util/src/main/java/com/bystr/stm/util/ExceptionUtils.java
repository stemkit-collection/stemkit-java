/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm.util;

import static com.bystr.stm.util.StreamUtils.streamOf;
import static java.util.stream.Collectors.joining;

public class ExceptionUtils {
    private ExceptionUtils() {}

    public static <R> R forceRuntimeWhenException(final ExceptionalSupplier<R> action) {
        try {
            return action.get();
        }
        catch (final Exception exception) {
            throw runtimeExceptionUnlessAlready(exception);
        }
    }

    public static void forceRuntimeWhenException(final ExceptionalRunnable action) {
        forceRuntimeWhenException(() -> {
            action.run();
            return null;
        });
    }

    public static RuntimeException runtimeExceptionUnlessAlready(final Exception exception) {
        if (exception instanceof RuntimeException) {
            return (RuntimeException) exception;
        }

        return new RuntimeException(makeMessage(exception.getClass().getName(), exception.getMessage()), exception);
    }

    public static String makeMessage(final String firstComponent, final String... remainingComponents) {
        return streamOf(firstComponent, remainingComponents)
            .map(ObjectUtils::inspectUnmarked)
            .collect(joining(": "));
    }

    public static <R> R ignoreWhenException(final Class<? extends Exception> exceptionClass, final ExceptionalSupplier<R> action) {
        try {
            return action.get();
        }
        catch (final Exception exception) {
            if (exceptionClass != null && exceptionClass.isInstance(exception) == false) {
                throw runtimeExceptionUnlessAlready(exception);
            }
        }

        return null;
    }

    public static <R> R ignoreWhenException(final ExceptionalSupplier<R> action) {
        return ignoreWhenException(null, action);
    }

    public static void ignoreWhenException(final Class<? extends Exception> exceptionClass, final ExceptionalRunnable... actions) {
        for (final ExceptionalRunnable action : actions) {
            if (action == null) {
                continue;
            }

            ignoreWhenException(exceptionClass, () -> {
                action.run();
                return null;
            });
        }
    }

    public static void ignoreWhenException(final ExceptionalRunnable... actions) {
        ignoreWhenException(null, actions);
    }
}
