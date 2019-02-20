/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm.util;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;

public final class FunctionalUtils {
    private FunctionalUtils() {}

    public static BiConsumer<String, String> valueListAdaptor(final BiConsumer<String, List<String>> consumer) {
        return (property, value) -> consumer.accept(property, value == null ? emptyList() : singletonList(value));
    }

    public static BiConsumer<String, List<String>> firstValueAdaptor(final BiConsumer<String, String> consumer) {
        return (property, value) -> consumer.accept(property, value == null || value.isEmpty() ? null : value.get(0));
    }

    public static <U> BinaryOperator<U> illegalStateCombiner() {
        return (first, second) -> {
            throw new IllegalStateException("Unexpected combiner invocation");
        };
    }
}
