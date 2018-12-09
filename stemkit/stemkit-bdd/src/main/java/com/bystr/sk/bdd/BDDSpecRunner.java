/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.sk.bdd;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.test.annotation.DirtiesContext.HierarchyMode;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mscharhag.oleaster.runner.Invokable;
import com.mscharhag.oleaster.runner.OleasterRunner;
import com.mscharhag.oleaster.runner.suite.Spec;

/**
 * {@link BDDSpecRunner} is JUnit runner based on {@link OleasterRunner} that lets
 * you write JUnit tests like you write tests with Jasmine (a popular Javascript
 * testing framework).
 * <p>
 * On top of what {@link OleasterRunner} provides, this runner also initializes
 * Spring framework for tests via keeping internal reference and delegating to
 * {@link SpringJUnit4ClassRunner}.It is a user responsibility to call static
 * methods {@link BDDSpecRunner#initSpring(Object testInstance)} and/or
 * {@link BDDSpecRunner#initMocks(Object testInstance)} before each test example.
 * <p>
 * Please see the declaration of class {@link MockingObjectSpecTest} for an example of how
 * to write BDD specs.
 * <p>
 * You will also need the following entries in your {@code pom.xml} file:
 * <pre>
 * {@code
 * <!-- Oleaster Matchers -->
 * <dependency>
 *     <groupId>com.mscharhag.oleaster</groupId>
 *     <artifactId>oleaster-matcher</artifactId>
 *     <version>0.1.2</version>
 *     <scope>test</scope>
 * </dependency>
 *
 * <!-- Oleaster JUnit runner -->
 * <dependency>
 *     <groupId>com.mscharhag.oleaster</groupId>
 *     <artifactId>oleaster-runner</artifactId>
 *     <version>0.1.2</version>
 *     <scope>test</scope>
 * </dependency>
 * }
 * </pre>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
 * <p>
 * @see com.mscharhag.oleaster.runner.OleasterRunner
 * @see com.mscharhag.oleaster.matcher.Matchers
 * @see com.mscharhag.oleaster.runner.StaticRunnerSupport#describe(String text, Invokable block)
 * @see com.mscharhag.oleaster.runner.StaticRunnerSupport#beforeEach(Invokable block)
 * @see com.mscharhag.oleaster.runner.StaticRunnerSupport#afterEach(Invokable block)
 * @see com.mscharhag.oleaster.runner.StaticRunnerSupport#it(String text, Invokable block)
*/
public class BDDSpecRunner extends OleasterRunner {
    /**
     * Instantiates a spec runner. Not to be used directly, but rather via {@code @RunWith}
     * annotation.
     * <p>
     * Example:
     * <pre>
     * {@code @RunWith(BDDSpecRunner.class)}
     * public class SimpleObjectSpecTest {
     *     ...
     * }
     * </pre>
     * @param testClass [{@link Class}]
     *     a test class, normally the one annotated with {@code @RunWith}
     * <p>
     * @exception InitializationError
    */
    public BDDSpecRunner(final Class<?> testClass) throws InitializationError {
        super(testClass);

        _springRunner = new SpringRunnerWrapper(testClass);
        registerRunner(testClass, this);
    }

    /**
     * This is a helper method that allows to avoid assignments and yet
     * invoke several methods for the same object. It invokes a specified
     * block with the object passes as a parameter, and returns that same
     * very object.
     * <p>
     * The action may invoke any methods it wants on that object, exactly
     * same way as would be with an explicit assignment.
     * <p>
     * Example:
     * <pre>
     * with(getThing(), thing -> {
     *     thing.doThis();
     *     thing.doThat();
     * });
     * </pre>
     * Compare to:
     * <pre>
     * ThingClass thing = getThing();
     * thing.doThis();
     * thing.doThat();
     * </pre>
     * Please notice that in the first example you do not need to mention
     * the "thing"'s type as it is automatically inferred from the return
     * type of {@code getThing()} method.
     * <p>
     * @param <T>
     *     the type of an object to invoke this method with.
     * <p>
     * @param object [{@code T}]
     *     an object to process
     * @param action [{@link Consumer}{@code <T>}]
     *     a code block to invoke with the specified object.
     * <p>
     * @return [{@code T}]
     *     the same object as the one in the first parameter.
    */
    public static <T> T with(final T object, final Consumer<T> action) {
        action.accept(object);
        return object;
    }

    /**
     * Initializes Spring context for a specified test instance. Normally should
     * be invoked as {@code initSpring(this)} from the {@code beforeEach()} block
     * in the top level {@code describe()} block of the test class.
     * <p>
     * For example:
     * <pre>
     * {@code @RunWith(BDDSpecRunner.class)}
     * public class SimpleObjectSpecTest {
     *     ...
     *     {
     *         describe("Sample spec", () -> {
     *             beforeEach(() -> {
     *                 initSpring(this)
     *             });
     *         });
     *     }
     * }
     * </pre>
     * <p>
     * @param testInstance [{@link Object}]
     *     a test instance.
    */
    public static void initSpring(final Object testInstance, final UnaryOperator<Boolean> contextDirtyProvider) {
        withRunnerFor(testInstance, runner -> {
            if (requireNonNull(contextDirtyProvider).apply(true) == true) {
                runner.markContextDirty();
            }

            runner.prepareTestInstance(testInstance);
        });
    }

    public static void initSpring(final Object testInstance) {
        initSpring(testInstance, identity());
    }

    public static void markSpringContextDirty(final Object testInstance) {
        withRunnerFor(testInstance, BDDSpecRunner::markContextDirty);
    }

    /**
     * Resets Mockito mocks by delegating to @{link MockitoAnnotations#initMocks(Object testInstance})}.
     * <p>
     * Normally should be invoked as {@code initMocks(this)} from the {@code beforeEach()} block in the
     * top level {@code describe()} block of the test class.
     * <p>
     * For example:
     * <pre>
     * {@code @RunWith(BDDSpecRunner.class)}
     * public class SimpleObjectSpecTest {
     *     ...
     *     {
     *         describe("Sample spec", () -> {
     *             beforeEach(() -> {
     *                 initMocks(this)
     *             });
     *         });
     *     }
     * }
     * </pre>
     * @param testInstance [{@link Object}]
     *     a test instance.
    */
    public static void initMocks(final Object testInstance) {
        MockitoAnnotations.initMocks(testInstance);
    }

    /**
     * Creates a Mockito mock for the specified type that would require any
     * method to be explicitly stubbed before it can be invoked. In case a
     * method is not stubbed, a runtime exception will be raised when its
     * invocation is attempted, with fully qualified method name together
     * with parameter types in the message.
     *
     * @param <T>
     *     a type to mock
     * <p>
     * @param targetClass [{@link Class}{@code <T>}]
     *     a class reference of the type to mock
     * <p>
     * @return [{@code T}]
     *     an instance of the specified class as a Mockito mock
    */
    public static <T> T strictMock(final Class<T> targetClass) {
        return strictMock(format("mock#%s", targetClass.getSimpleName()), targetClass);
    }

    public static <T> T strictMock(final String mockName, final Class<T> targetClass) {
        final T mock = Mockito.mock(targetClass, invocation -> {
            throw new RuntimeException(format("%s: %s#%s(%s) is not stubbed",
                mockName,
                targetClass.getName(),
                invocation.getMethod().getName(),
                Stream.of(invocation.getMethod().getParameterTypes())
                    .map(Class::getName)
                    .collect(joining(", "))
            ));
        });

        Mockito.doReturn(mockName)
            .when(mock).toString();

        return mock;
    }

    /**
     * A helper method to make it easier to reference and cast arguments when using
     * {@link Mockito#doAnswer()}. For this method to automatically infer the type
     * it must be used in assignment operator. It eliminates the need for casting
     * generic types needed when {@link InvocationOnMock#getArgumentAt(int, Class)}
     * is used directly. That cast is needed because generic types do not provide
     * proper class info ({@code List<String>.class} is not currently supported).
     * <p>
     * Example:
     * <pre>
     * import static com.bystr.sk.bdd.BDDSpecRunner.mockMethodArgument;
     * ...
     * doAnswer(invocation -> {
     *     final String name = mockMethodArgument(0, invocation);
     *     final Date date = mockMethodArgument(1, invocation);
     *     ...
     * })
     * .when(...)
     * </pre>
     * @param <R>
     *     a type to cast the referenced argument to
     * <p>
     * @param index [{@code int}]
     *     argument index, starting from 0
     * @param invocation [{@link InvocationOnMock}]
     *     the invocation object passed from {@link Mockito#doAnswer()}
     * <p>
     * @return [{@code R}]
     *     an object at the referenced argument properly cast to the target type
    */
    public static <R extends Object> R mockMethodArgument(final int index, final InvocationOnMock invocation) {
        return invocation.getArgument(index);
    }

    @Override
    protected void runChild(final Spec spec, final RunNotifier notifier) {
        final Description specDescription = describeChild(spec);
        final EachTestNotifier eachNotifier = new EachTestNotifier(notifier, specDescription);

        try {
            super.runChild(spec, notifier);
        }
        catch (final Throwable throwable) {
            printNestedErrors("", throwable, () -> {
                System.err.println(specDescription);
            });

            final Throwable cause = throwable.getCause();
            eachNotifier.addFailure(cause == null ? throwable : cause);
        }
    }

    private static void printNestedErrors(final String offset, final Throwable error, final Runnable printer) {
        if (error != null) {
            System.err.println(offset + "ERROR: " + error);
            printer.run();

            Stream.of(error.getStackTrace()).forEach(item -> {
                System.err.println(offset + "| " + item);
            });

            printNestedErrors(offset + "  ", error.getCause(), () -> {});
        }
    }

    private void prepareTestInstance(final Object testInstance) throws Exception {
        _springRunner.contextManager().prepareTestInstance(testInstance);
    }

    private void markContextDirty() {
        _springRunner.contextManager().getTestContext().markApplicationContextDirty(HierarchyMode.EXHAUSTIVE);
    }

    private static void registerRunner(final Class<?> testClass, final BDDSpecRunner runner) {
        __runners.putIfAbsent(testClass.getName(), runner);
    }

    private static BDDSpecRunner runnerFor(final Object testInstance) {
        final String testClassName = testInstance.getClass().getName();
        final BDDSpecRunner runner = __runners.get(testClassName);

        if (runner == null) {
            throw new RuntimeException(format("No runner: %s", testClassName));
        }

        return runner;
    }

    private interface RunnerConsumer {
        void accept(final BDDSpecRunner runner) throws Exception;
    }

    private static void withRunnerFor(final Object testInstance, final RunnerConsumer action) {
        try {
            action.accept(runnerFor(testInstance));
        }
        catch (final Exception exception) {
            throw new RuntimeException(String.format(">>> BDDSpecRunner: %s <<<", exception.getMessage()), exception);
        }
    }

    private static final HashMap<String, BDDSpecRunner> __runners = new HashMap<>();
    private final SpringRunnerWrapper _springRunner;

    private static class SpringRunnerWrapper extends SpringJUnit4ClassRunner {
        public SpringRunnerWrapper(final Class<?> testClass) throws InitializationError {
            super(testClass);
        }

        TestContextManager contextManager() {
            return super.getTestContextManager();
        }

        @Override
        protected void collectInitializationErrors(final List<Throwable> errors) {
            return;
        }
    }
}
