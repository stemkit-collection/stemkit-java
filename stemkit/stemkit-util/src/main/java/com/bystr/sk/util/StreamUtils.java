/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.sk.util;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtils {
    private StreamUtils() {}

    public static <T> Stream<T> streamOf(final T[] values) {
        return Stream.of(values);
    }

    @SafeVarargs
    public static <T> Stream<T> streamOf(final T value, final T... values) {
        if (values == null || values.length == 0) {
            return Stream.of(value);
        }

        return Stream.concat(Stream.of(value),  Stream.of(values));
    }

    public static <T> Stream<T> streamFrom(final Iterator<T> iterator) {
        return streamFrom(() -> iterator);
    }

    public static <T> Stream<T> streamFrom(final Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
