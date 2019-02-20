/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm.util;

import static com.bystr.stm.util.ExceptionUtils.forceRuntimeWhenException;
import static com.bystr.stm.util.ExceptionUtils.makeMessage;
import static com.bystr.stm.util.StreamUtils.streamOf;
import static java.util.stream.Collectors.joining;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Holder<T> {
    private Exception _exception;
    private T _object;

    public Holder() {
        clear();
    }

    public Holder(final T object) {
        set(object);
    }

    public T get() {
        if (hasException() == false) {
            return _object;
        }

        throw illegalState("Unexpected exception", exception().getClass().getName(), exception().getMessage());
    }

    public Optional<T> getOptional() {
        return Optional.ofNullable(get());
    }

    public final Holder<T> set(final T object) {
        _exception = null;
        _object = object;

        return this;
    }

    public Holder<T> setException(final Exception exception) {
        if (exception != null) {
            _object = null;
        }

        _exception = exception;
        return this;
    }

    public final Holder<T> clear() {
        return set(null);
    }

    public boolean isEmpty() {
        return hasValue() == false && hasException() == false;
    }

    public boolean hasValue() {
        return _object != null;
    }

    public boolean hasException() {
        return _exception != null;
    }

    @SafeVarargs
    public final boolean hasException(final Class<? extends Exception> exceptionClass, final Class<? extends Exception>... exceptionClasses) {
        if (hasException() == false) {
            return false;
        }

        if (exceptionClass.isInstance(_exception) == true) {
            return true;
        }

        if (exceptionClasses.length == 0) {
            return false;
        }

        return hasException(exceptionClasses);
    }

    public final boolean hasException(final Class<? extends Exception>[] exceptionClasses) {
        if (hasException() == false) {
            return false;
        }

        if (exceptionClasses.length == 0) {
            return true;
        }

        for(final Class<? extends Exception> exceptionClass : exceptionClasses) {
            if (exceptionClass.isInstance(_exception) == true) {
                return true;
            }
        }

        return false;
    }

    @SafeVarargs
    public final Holder<T> ensureException(final Class<? extends Exception> exceptionClass, final Class<? extends Exception>... exceptionClasses) {
        if (hasException() == false) {
            throw illegalState("No expected exception", classNames(exceptionClass, exceptionClasses));
        }

        if (hasException(exceptionClass, exceptionClasses) == true) {
            return this;
        }

        throw illegalState("Wrong exception", "got", exception().getClass().getName(), "need", classNames(exceptionClass, exceptionClasses));
    }

    public Holder<T> ensureException(final boolean presence) {
        if (presence == true) {
            exception();
        }
        else {
            get();
        }

        return this;
    }

    public Holder<T> ensureValue(final boolean presence) {
        if (presence == true) {
            if (hasValue() == false) {
                ensureException(false);
                throw illegalState("No expected value");
            }
        }
        else {
            if (hasValue() == true) {
                throw illegalState("Unexpected value", _object.toString());
            }
        }

        return this;
    }

    public Exception exception() {
        if (hasException() == true) {
            return _exception;
        }

        throw illegalState("No expected exception");
    }

    public Holder<T> acceptFrom(final ExceptionalSupplier<T> supplier) {
        clear();

        try {
            _object = supplier.get();
        }
        catch (final Exception exception) {
            _exception = exception;
        }

        return this;
    }

    private final Holder<T> thisHolder() {
        return this;
    }

    public static interface ExceptionProcessor<E extends Exception, T> {
        Holder<T> thenRaiseForcingRuntime();
        Holder<T> thenReplaceWithCause();
        Holder<T> thenIgnore();

        Holder<T> thenProcess(final Consumer<E> consumer);
        Holder<T> thenRaise(final Function<E, RuntimeException> converter);
        Holder<T> thenSetException(final Function<E, Exception> converter);
        Holder<T> thenSet(final Function<E, T> converter);
        Holder<T> thenSet(final T object);
    }

    private abstract class AbstractExceptionProcessor<E extends Exception> implements ExceptionProcessor<E, T> {
        @Override
        public Holder<T> thenIgnore() {
            return thisHolder();
        }

        @Override
        public Holder<T> thenRaiseForcingRuntime() {
            return thenRaise(ExceptionUtils::runtimeExceptionUnlessAlready);
        }
    }

    private class MissingExceptionProcessor<E extends Exception> extends AbstractExceptionProcessor<E> {
        @Override
        public Holder<T> thenProcess(final Consumer<E> consumer) {
            return thisHolder();
        }

        @Override
        public Holder<T> thenRaise(final Function<E, RuntimeException> converter) {
            return thisHolder();
        }

        @Override
        public Holder<T> thenReplaceWithCause() {
            return thisHolder();
        }

        @Override
        public Holder<T> thenSetException(final Function<E, Exception> converter) {
            return thisHolder();
        }

        @Override
        public Holder<T> thenSet(final Function<E, T> converter) {
            return thisHolder();
        }

        @Override
        public Holder<T> thenSet(final T object) {
            return thisHolder();
        }
    }

    private class AvailableExceptionProcessor<E extends Exception> extends AbstractExceptionProcessor<E> {
        @Override
        @SuppressWarnings("unchecked")
        public Holder<T> thenProcess(final Consumer<E> consumer) {
            final Holder<T> holder = thisHolder();

            consumer.accept((E) holder.exception());
            return holder;
        }

        @Override
        public Holder<T> thenRaise(final Function<E, RuntimeException> converter) {
            return thenProcess(exception -> raiseUnlessNull(converter.apply(exception)));
        }

        @Override
        public Holder<T> thenReplaceWithCause() {
            return thenProcess(exception -> {
                final Throwable cause = exception.getCause();

                if (cause == null) {
                    _exception = illegalState("No cause for exception", exception.getClass().getName(), exception.getMessage());
                    return;
                }

                if (cause instanceof Exception) {
                    _exception = (Exception) cause;
                    return;
                }

                _exception = illegalState(
                    "Unsupported cause for exception",
                    cause.getClass().getName(),
                    exception.getClass().getName(),
                    exception.getMessage()
                );
            });
        }

        @Override
        public Holder<T> thenSetException(final Function<E, Exception> converter) {
            return thenProcess(exception -> thisHolder().setException(converter.apply(exception)));
        }

        @Override
        public Holder<T> thenSet(final Function<E, T> converter) {
            return thenProcess(exception -> thisHolder().set(converter.apply(exception)));
        }

        @Override
        public Holder<T> thenSet(final T object) {
            return thisHolder().set(object);
        }
    }

    @SafeVarargs
    public final ExceptionProcessor<Exception, T> whenException(final Class<? extends Exception>... exceptionClasses) {
        if (hasException(exceptionClasses) == true) {
            return new AvailableExceptionProcessor<Exception>();
        }

        return new MissingExceptionProcessor<Exception>();
    }

    public <E extends Exception> ExceptionProcessor<E, T> whenException(final Class<E> exceptionClass) {
        if (hasException(exceptionClass) == true) {
            return new AvailableExceptionProcessor<E>();
        }

        return new MissingExceptionProcessor<E>();
    }

    @SafeVarargs
    public final ExceptionProcessor<Exception, T> unlessException(final Class<? extends Exception> exceptionClass, final Class<? extends Exception>... exceptionClasses) {
        if (hasException(exceptionClass, exceptionClasses) == true) {
            return new MissingExceptionProcessor<Exception>();
        }

        return new AvailableExceptionProcessor<Exception>();
    }

    public static interface EmptyProcessor<T> {
        Holder<T> thenSetException(final Supplier<Exception> supplier);
        Holder<T> thenRaise(final Supplier<RuntimeException> supplier);
        Holder<T> thenSet(final Supplier<T> supplier);
        Holder<T> thenSet(final T object);
        Holder<T> thenRun(final Runnable runnable);

        // This form is here just to make it possible to use assignment as simple
        // lambda expression with this method. Otherwise compiler complains that
        // invocation is not applicable to method declaration.
        //
        <R> Holder<T> thenRun(final Supplier<R> supplier);
    }

    private abstract class AbstractEmptyProcessor implements EmptyProcessor<T> {
        @Override
        public Holder<T> thenSet(final Supplier<T> supplier) {
            return thenSet(supplier.get());
        }

        @Override
        public <R> Holder<T> thenRun(final Supplier<R> supplier) {
            // IMPORTANT: do not change invocation to method reference as instructed by
            // SonarQube or other code analysis tools. It will cause a stack overflow
            // as exactly same method will be called recursively. Same will happen when
            // changed to simple lambda, so do not do it.
            //
            return thenRun(() -> {
                supplier.get();
            });
        }
    }

    private class NotEmptyProcessor extends AbstractEmptyProcessor {
        @Override
        public Holder<T> thenSet(final T object) {
            return thisHolder();
        }

        @Override
        public Holder<T> thenRun(final Runnable runnable) {
            return thisHolder();
        }

        @Override
        public Holder<T> thenRaise(final Supplier<RuntimeException> supplier) {
            return thisHolder();
        }

        @Override
        public Holder<T> thenSetException(final Supplier<Exception> supplier) {
            return thisHolder();
        }
    }

    private class IndeedEmptyProcessor extends AbstractEmptyProcessor {
        @Override
        public Holder<T> thenSet(final T object) {
            return thisHolder().set(object);
        }

        @Override
        public Holder<T> thenRun(final Runnable runnable) {
            runnable.run();
            return thisHolder();
        }

        @Override
        public Holder<T> thenRaise(final Supplier<RuntimeException> supplier) {
            return raiseUnlessNull(supplier.get());
        }

        @Override
        public Holder<T> thenSetException(final Supplier<Exception> supplier) {
            return thisHolder().setException(supplier.get());
        }
    }

    private Holder<T> raiseUnlessNull(final RuntimeException exception) {
        if (exception != null) {
            throw exception;
        }

        return this;
    }

    public EmptyProcessor<T> whenEmpty() {
        if (hasValue() == false) {
            return new IndeedEmptyProcessor();
        }

        return new NotEmptyProcessor();
    }

    public Holder<T> filter(final Predicate<T> predicate) {
        if (hasValue() == true) {
            if (predicate.test(get()) == false) {
                clear();
            }
        }

        return this;
    }

    public Holder<T> tap(final ExceptionalConsumer<T> consumer) {
        if (hasValue() == true) {
            forceRuntimeWhenException(() -> consumer.accept(get()));
        }

        return this;
    }

    public Holder<T> notap(final ExceptionalConsumer<T> consumer) {
        return this;
    }

    public <R> R map(final ExceptionalFunction<T, R> converter) {
        if (hasValue() == true) {
            return forceRuntimeWhenException(() -> converter.apply(get()));
        }

        return null;
    }

    public Holder<T> replace(final ExceptionalFunction<T, T> converter) {
        if (hasValue() == true) {
            final T value = get();
            acceptFrom(() -> converter.apply(value));
        }

        return this;
    }

    public T orElse(final T fallback) {
        return (hasValue() == true) ? get() : fallback;
    }

    public T orElseGet(final Supplier<T> fallbackSupplier) {
        return (hasValue() == true) ? get() : fallbackSupplier.get();
    }

    public static <R> Holder<R> hold(final R object) {
        return new Holder<R>(object);
    }

    public static <R> Holder<R> holdFrom(final ExceptionalSupplier<R> supplier) {
        return (new Holder<R>()).acceptFrom(supplier);
    }

    public static Holder<Object> holdFrom(final ExceptionalRunnable runnable) {
        return holdFrom(() -> {
            runnable.run();
            return null;
        });
    }

    private RuntimeException illegalState(final String... components) {
        return new IllegalStateException(makeMessage(this.getClass().getName(), components), _exception);
    }

    private static String classNames(final Class<?> firstClass, final Class<?>... otherClasses) {
        return streamOf(firstClass, otherClasses)
            .map(Class::getName)
            .collect(joining(", "));
    }
}
