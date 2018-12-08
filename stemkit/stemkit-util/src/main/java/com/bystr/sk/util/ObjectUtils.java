/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.sk.util;

import static com.bystr.sk.util.ExceptionUtils.forceRuntimeWhenException;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.text.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Provides a set of useful utility (static) methods that make possible to
 * avoid direct assignments as much as possible. The resulting code becomes
 * more compact and expressive, grouping related operations in Java code
 * blocks.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
public class ObjectUtils {
    private ObjectUtils() {}

    /**
     * Accepts an object as a parameter and returns this very object. Before
     * returning though, unless object is {@code null}, it invokes the code
     * block normally specified in a form of a lambda expression, passing
     * the object as the only parameter.
     * <p>
     * Example:
     * <pre>
     * import static com.bystr.sk.util.ObjectUtils.tap;
     * ...
     * public String userById(final UUID userId) {
     *     return tap(db.findUser(userId), user -> {
     *         log.info("Found user: " + user.getName());
     *     });
     * }
     * </pre>
     * @param <T>
     *     the type of an object to tap, normally inferred automatically
     * <p>
     * @param object [{@code T}]
     *     an object to tap, or {@code null}
     * @param consumer [{@link Consumer}{@code <T>}]
     *     a code block to execute with that object, unless {@code null}
     * <p>
     * @return [{@code T}]
     *     the original object passed as a first parameter, possibly {@code null}
    */
    public static <T> T tap(final T object, final Consumer<T> consumer) {
        // This form of tap (with Consumer) is the most used, hence making it
        // a primary implementation expressing the one accepting Runnable via
        // this one and not the other way around.
        //
        if (object != null) {
            consumer.accept(object);
        }

        return object;
    }

    /**
     * Same as {@link #tap(Object, Consumer)}, though the action is an instance of
     * {@link Runnable} normally passed as a lambda expression without parameters.
     * Used when a reference to the object being tapped is not needed &mdash; you
     * either have it from an outer scope or just want to get action invoked when
     * the object is not {@code null}.
     * <p>
     * Example:
     * <pre>
     * import static com.bystr.sk.util.ObjectUtils.tap;
     * ...
     * public String userById(final UUID userId) {
     *     return tap(db.findUser(userId), () -> {
     *         log.info("Found user for id: " + userId);
     *     });
     * }
     * </pre>
     * @param <T>
     *     the type of an object to tap, normally inferred automatically
     * <p>
     * @param object [{@code T}]
     *     an object to tap, or {@code null}
     * @param action [{@link Runnable}]
     *     a code block to execute if the object is not {@code null}
     * <p>
     * @return [{@code T}]
     *     the original object passed as a first parameter, possibly {@code null}
     * <p>
     * @see #tap(Object, Consumer)
    */
    public static <T> T tap(final T object, final Runnable action) {
        return tap(object, o -> action.run());
    }

    /**
     * Accepts an object as a parameter and invokes a mapping function to
     * produce another object that gets returned, possibly of a completely
     * different type.
     * <p>
     * When input object is {@code null}, the mapping code does not get
     * invoked returning {@code null} of the output type right away. The
     * mapping function is normally passed as a lambda expression with a
     * single parameter, to return a corresponding object of the output type.
     * <p>
     * Example:
     * <pre>
     * import static com.bystr.sk.util.ObjectUtils.map;
     * ...
     * public User loginUser() {
     *     return map(loginUserName(), userName -> {
     *         logger.info("Looking up user " + userName);
     *         return db.findUser(userName);
     *     });
     * }
     * </pre>
     * @param <T>
     *     the type of an object to map, normally inferred automatically
     * @param <R>
     *     the type of an object to return, normally inferred automatically
     * <p>
     * @param object [{@code T}]
     *     an object to map, or {@code null}
     * @param mapper [{@link Function}{@code <T, R>}]
     *     a code block to use for mapping from input object to output object,
     *     not invoked if {@code null}
     * <p>
     * @return [{@code R}]
     *     an output object of type {@code R} corresponding to the original
     *     object of type {@code T} passed as a first parameter
    */
    public static <T, R> R map(final T object, final Function<T, R> mapper) {
        return object == null ? null : mapper.apply(object);
    }

    /**
     * Same as {@link #map(Object, Function)}, though the code to make an
     * output object is specified as an instance of {@link Supplier} which
     * does not get a parameter. It is convenient when an object to map is
     * available from an outer scope, the code block will be invoked only the
     * input object is not {@code null}.
     * <p>
     * Example:
     * <pre>
     * import static com.bystr.sk.util.ObjectUtils.map;
     * ...
     * public User userByName(userName) {
     *     return map(userName, () -> {
     *         logger.info("Looking up user " + userName);
     *         return db.findUser(userName);
     *     });
     * }
     * </pre>
     * @param <T>
     *     the type of an object to map, normally inferred automatically
     * @param <R>
     *     the type of an object to return, normally inferred automatically
     * <p>
     * @param object [{@code T}]
     *     an object to map, or {@code null}
     * @param supplier [{@link Supplier}{@code <R>}]
     *     a code block to use for making an output object, not invoked
     *     if parameter object is {@code null}
     * <p>
     * @return [{@code R}]
     *     an output object of type {@code R} corresponding to the original
     *     object of type {@code T} passed as a first parameter
     * <p>
     * @see #map(Object, Function)
    */
    public static <T, R> R map(final T object, final Supplier<R> supplier) {
        return map(object, o -> supplier.get());
    }

    /**
     * If {@code null} object is passed returns the fallback value immediately.
     * Otherwise passes the object to the specified predicate code block and
     * returns the value from that block.
     * <p>
     * @param <T>
     *     the type of an object to check, normally inferred automatically
     * <p>
     * @param object [{@code T}]
     *     an object to check, or {@code null}
     * @param fallback
     *     a boolean value to return when {@code null} is passed for an object
     * @param predicate [{@link Predicate}{@code <T>}]
     *     a code block to use for checking the object, not invoked if {@code null}
     * <p>
     * @return [{@code boolean}]
     *     the fallback value if the object is {@code null}, otherwise the value from
     *     the specified predicate block
    */
    public static <T> boolean check(final T object, final boolean fallback, final Predicate<T> predicate) {
        return object == null ? fallback : predicate.test(object);
    }

    public static <T> boolean check(final T object, final boolean fallback, final BooleanSupplier supplier) {
        return check(object, fallback, sameObject -> supplier.getAsBoolean());
    }

    /**
     * Accepts an object as a parameter and passes it to the specified
     * predicate block. Unless {@code null}, in which case {@code null}
     * is immediately returned. If the predicate block yields {@code true}
     * the input object gets returned from this method, {@code null}
     * otherwise.
     * <p>
     * Example:
     * <pre>
     * import static com.bystr.sk.util.ObjectUtils.filter;
     * ...
     * public User userById(final UUID userId) {
     *     return filter(db.findUserById(), user -> {
     *         return checker.mayAccessUser(user);
     *     });
     * }
     * </pre>
     * @param <T>
     *     the type of an object to filter, normally inferred automatically
     * <p>
     * @param object [{@code T}]
     *     an object to filter, or {@code null}
     * @param checker [{@link Predicate}{@code <T>}]
     *     a code block to get the input object and produce {@code boolean},
     *     with {@code true} causing the object to be returned from this
     *     method, or {@code null} when {@code false}
     * <p>
     * @return [{@code T}]
     *     the original object or {@code null} if the predicate code block
     *     returns {@code false}
    */
    public static <T> T filter(final T object, final Predicate<T> checker) {
        return object == null || checker.test(object) == false ? null : object;
    }

    /**
     * Same as {@link #tap(Object, Consumer)} with even {@code null} values
     * causing the block invocation. To accommodate {@code null} values the
     * code block is invoked with the original object (or {@code null})
     * wrapped in {@link Optional#ofNullable(Object)}.
     * <p>
     * @param <T>
     *     the type of an object to tap, normally inferred automatically
     * <p>
     * @param object [{@code T}]
     *     an object to tap, or {@code null}
     * @param consumer [{@link Consumer}<{@link Optional}{@code <T>}>]
     *     a code block to execute with that object wrapped in
     *     {@link Optional#ofNullable(Object)}
     * <p>
     * @return [{@link Optional}{@code <T>}]
     *     the original object passed as a first parameter wrapped in
     *     {@link Optional#ofNullable(Object)}
     * <p>
     * @see #tap(Object, Consumer)
     * @see #tap(Object, Runnable)
    */
    public static <T> T tapNullable(final T object, final Consumer<Optional<T>> consumer) {
        final Optional<T> optional = Optional.ofNullable(object);
        consumer.accept(optional);

        return object;
    }

    /**
     * Same as {@link #map(Object, Function)} with even {@code null} values
     * causing the block invocation. To accommodate {@code null} values the
     * code block is invoked with the original object (or {@code null})
     * wrapped in {@link Optional#ofNullable(Object)}.
     * <p>
     * @param <T>
     *     the type of an object to map, normally inferred automatically
     * @param <R>
     *     the type of an object to return, normally inferred automatically
     * <p>
     * @param object [{@code T}]
     *     an object to map, or {@code null}
     * @param mapper [{@link Function}<{@link Optional}{@code <T>}, {@code R}>]
     *     a code block to execute with that object wrapped in
     *     {@link Optional#ofNullable(Object)} and to return another object of type
     *     {@code R}
     * <p>
     * @return [{@link Optional}{@code <R>}]
     *     an output object of type {@code R} wrapped in
     *     {@link Optional#ofNullable(Object)} corresponding to the original
     *     object of type {@code T} passed as a first parameter
     * <p>
     * @see #map(Object, Function)
    */
    public static <T, R> R mapNullable(final T object, final Function<Optional<T>, R> mapper) {
        return mapper.apply(Optional.ofNullable(object));
    }

    @SafeVarargs
    public static <T> T firstOrGet(final T object, final ExceptionalSupplier<T>... suppliers) {
        if (object != null) {
            return object;
        }

        for (final ExceptionalSupplier<T> supplier : suppliers) {
            final T replacement = forceRuntimeWhenException(supplier);

            if (replacement != null) {
                return replacement;
            }
        }

        throw new NullPointerException("No object or replacement");
    }

    public static <T, E extends RuntimeException> T firstOrThrow(final T object, final Supplier<E> exceptionSupplier) {
        if (object != null) {
            return object;
        }

        throw firstOrGet(exceptionSupplier.get());
    }

    public static class MapHolder<K, V> {
        public final Map<K, V> map = new HashMap<>();

        public MapHolder<K, V> put(final K key, final V value) {
            this.map.put(key, value);
            return this;
        }
    }

    public static <K, V> MapHolder<K, V> makeMap() {
        return new MapHolder<>();
    }

    @SafeVarargs
    public static <T> Set<T> makeSet(final T... items) {
        return new HashSet<>(asList(items));
    }

    public static String inspect(final Collection<?> collection) {
        return deepInspect(0, collection);
    }

    public static String deepInspect(final Collection<?> collection) {
        return deepInspect(-1, collection);
    }

    public static String deepInspect(final int depth, final Collection<?> items) {
        return stringify(items, object ->
            items.stream()
                .map(figureInspector(depth))
                .collect(joining(", ", prefix(items.size(), "["), "]"))
        );
    }

    public static String inspect(final Map<?, ?> map) {
        return deepInspect(0, map);
    }

    public static String deepInspect(final Map<?, ?> map) {
        return deepInspect(-1, map);
    }

    public static String deepInspect(final int depth, final Map<?, ?> items) {
        return stringify(items, object ->
            items.entrySet().stream()
                .map(entry ->
                    format("%s=>%s",
                        figureInspector(0).apply(entry.getKey()),
                        figureInspector(depth).apply(entry.getValue())
                    )
                )
                .collect(joining(", ", prefix(items.size(), "{"), "}"))
        );
    }

    private static String prefix(final int size, final String basePrefix) {
        return size < 2 ? basePrefix : format("%s%d: ", basePrefix, size);
    }

    private static Function<Object, String> figureInspector(final int depth) {
        return object -> {
            if (object instanceof String) {
                return inspect((String) object);
            }

            if (depth != 0) {
                final int lessDepth = depth > 0 ? depth - 1 : depth;

                if (object instanceof Collection<?>) {
                    return deepInspect(lessDepth, (Collection<?>) object);
                }

                if (object instanceof Map<?, ?>) {
                    return deepInspect(lessDepth, (Map<?, ?>) object);
                }
            }

            return inspect(object);
        };
    }

    public static String inspect(final String string) {
        return stringify(string, object -> format("\"%s\"", escapeJava(string)));
    }

    public static String inspectUnmarked(final String string) {
        return stringify(string, object -> isBlank(string) ? "<blank>" : escapeJava(string));
    }

    private static final class Depot {
        static final Class<?>[] literalClasses = {
            Integer.class,
            Number.class
        };
    }

    public static String inspect(final Object item) {
        return stringify(item, object -> {
            for (final Class<?> aClass : Depot.literalClasses) {
                if (aClass.isInstance(item) == true) {
                    return item.toString();
                }
            }

            return format("<%s:%s>", object.getClass().getSimpleName(), object);
        });
    }

    private static String stringify(final Object object, final Function<Object, String> converter) {
        return object == null ? "<null>" : converter.apply(object);
    }

    // This is just to make coverage tools happy.
    static {
        new ObjectUtils();
    }
}
