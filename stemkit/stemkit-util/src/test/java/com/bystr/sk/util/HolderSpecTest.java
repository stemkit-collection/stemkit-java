package com.bystr.sk.util;

import static com.bystr.sk.util.Holder.hold;
import static com.bystr.sk.util.Holder.holdFrom;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.mscharhag.oleaster.matcher.util.Expectations.fail;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.afterEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.beforeEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.describe;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.it;

import java.util.MissingResourceException;

import org.junit.runner.RunWith;

import com.bystr.sk.bdd.BDDSpecRunner;
import com.bystr.sk.util.Holder;

@RunWith(BDDSpecRunner.class)
public class HolderSpecTest {
    Holder<String> sampleHolder;

    {
        describe(Holder.class.getName(), () -> {
            afterEach(() -> sampleHolder = null);

            describe("when created empty", () -> {
                beforeEach(() -> {
                    sampleHolder = new Holder<>();
                });

                it("returns true for isEmpty()", () -> expect(sampleHolder.isEmpty()).toBeTrue());
                it("returns false for hasValue()", () -> expect(sampleHolder.hasValue()).toBeFalse());
                it("returns null for get()", () -> expect(sampleHolder.get()).toBeNull());

                it("returns false for hasException()", () ->
                    expect(sampleHolder.hasException()).toBeFalse());

                it("returns false for hasException(RuntimeException.class)", () ->
                    expect(sampleHolder.hasException(RuntimeException.class)).toBeFalse());

                it("taps into non-null value", () -> {
                    hold("Hello").tap(sampleHolder::set);
                    expect(sampleHolder.get()).toEqual("Hello");
                });

                it("throws for ensureValue(true)", () -> {
                    try {
                        sampleHolder.ensureValue(true);
                        fail("No expected exception");
                    }
                    catch (IllegalStateException exception) {}
                });

                it("does not throw for ensureValue(false)", () -> {
                    try {
                        sampleHolder.ensureValue(false);
                    }
                    catch (final Exception exception) {
                        fail("Unexpected exception");
                    }
                });

                it("throws illegal state when asked for exception", () -> {
                    try {
                        sampleHolder.exception();
                        fail("No expected exception");
                    }
                    catch (final Exception exception) {
                        expect(exception.getClass()).toEqual(IllegalStateException.class);
                    }
                });

                it("throws when ensureException() with thrown", () -> {
                    try {
                        sampleHolder.ensureException(IllegalArgumentException.class);
                        fail("No expected exception");
                    }
                    catch (final Exception exception) {
                        expect(exception.getClass()).toEqual(IllegalStateException.class);
                    }
                });

                it("throws when ensureException(true)", () -> {
                    try {
                        sampleHolder.ensureException(true);
                        fail("No expected exception");
                    }
                    catch (final Exception exception) {
                        expect(exception.getClass()).toEqual(IllegalStateException.class);
                    }
                });

                it("does not throw when ensureException(false)", () -> {
                    try {
                        sampleHolder.ensureException(false);
                    }
                    catch (final Exception exception) {
                        fail("Unexpected exception");
                    }
                });

                it("does not change value when mapping exception", () ->
                    expect(sampleHolder.whenException().thenSet(exception -> "OOO").hasValue()).toBeFalse());

                it("does not invoke lambda for whenException()", () -> {
                    final boolean processed[] = {false};

                    sampleHolder.whenException().thenProcess(exception -> processed[0] = true);
                    expect(processed[0]).toBeFalse();
                });

                it("invokes lambda for whenEmpty()", () -> {
                    final boolean processed[] = {false};

                    sampleHolder.whenEmpty().thenRun(() -> processed[0] = true);
                    expect(processed[0]).toBeTrue();
                });

                it("returns orElse() value", () ->
                    expect(sampleHolder.orElse("UUU")).toEqual("UUU"));

                it("returns orElseGet() value", () ->
                    expect(sampleHolder.orElseGet(() -> "UUU")).toEqual("UUU"));

                it("does not invoke for raise when exception", () -> {
                    sampleHolder.whenException().thenRaise(exception -> {
                        fail("Unexpected block invocation");
                        return null;
                    });

                    sampleHolder.whenException(RuntimeException.class).thenRaise(exception -> {
                        fail("Unexpected block invocation");
                        return null;
                    });
                });

                it("leaks non-checked exception as is", () -> {
                    final Exception originalException = new IllegalArgumentException("to be leaked");

                    try {
                        sampleHolder.acceptFrom(() -> {
                            throw originalException;
                        });

                        sampleHolder.whenException().thenRaiseForcingRuntime();
                        fail("No expected exception");
                    }
                    catch (final Exception exception) {
                        expect(exception == originalException).toBeTrue();
                    }
                });

                it("leaks checked exception wrapped in RuntimeException", () -> {
                    final Exception originalException = new Exception("to be leaked");

                    try {
                        sampleHolder.acceptFrom(() -> {
                            throw originalException;
                        });

                        sampleHolder.whenException().thenRaiseForcingRuntime();
                        fail("No expected exception");
                    }
                    catch (final Exception exception) {
                        expect(exception == originalException).toBeFalse();
                        expect(exception.getCause() == originalException).toBeTrue();
                    }
                });

                it("does not leak when no exception", () -> {
                    try {
                        sampleHolder.whenException().thenRaiseForcingRuntime();
                    }
                    catch (final Exception exception) {
                        fail("Unexpected exception");
                    }
                });

                it("replaces exception with cause", () -> {
                    sampleHolder.acceptFrom(() -> {
                        throw new UnsupportedOperationException(new NumberFormatException());
                    });

                    sampleHolder
                        .ensureException(UnsupportedOperationException.class)
                        .whenException().thenReplaceWithCause()
                        .ensureException(NumberFormatException.class);
                });

                it("replaces exception with illegal state for no cause", () -> {
                    sampleHolder.acceptFrom(() -> {
                        throw new UnsupportedOperationException();
                    });

                    sampleHolder
                        .ensureException(UnsupportedOperationException.class)
                        .whenException().thenReplaceWithCause()
                        .ensureException(IllegalStateException.class)
                        .whenException().thenProcess(exception -> {
                            expect(exception.getMessage()).toContain("No cause");
                        });
                });

                it("replaces exception with illegal state for not supported", () -> {
                    sampleHolder.acceptFrom(() -> {
                        throw new UnsupportedOperationException(new Error());
                    });

                    sampleHolder
                        .ensureException(UnsupportedOperationException.class)
                        .whenException().thenReplaceWithCause()
                        .ensureException(IllegalStateException.class)
                        .whenException().thenProcess(exception -> {
                            expect(exception.getMessage()).toContain("Unsupported cause");
                        });
                });

                it("replaces blank exception message component with a marker", () -> {
                    sampleHolder.acceptFrom(() -> {
                        throw new UnsupportedOperationException("   ");
                    });

                    sampleHolder
                        .ensureException(UnsupportedOperationException.class)
                        .whenException().thenReplaceWithCause()
                        .whenException().thenProcess(exception -> {
                            expect(exception.getMessage()).toContain("<blank>");
                        });
                });

                it("replaces null exception message component with a marker", () -> {
                    sampleHolder.acceptFrom(() -> {
                        throw new UnsupportedOperationException();
                    });

                    sampleHolder
                        .ensureException(UnsupportedOperationException.class)
                        .whenException().thenReplaceWithCause()
                        .whenException().thenProcess(exception -> {
                            expect(exception.getMessage()).toContain("<null>");
                        });
                });
            });

            describe("when exception is thrown when accepting value", () -> {
                beforeEach(() -> {
                    sampleHolder = new Holder<>();
                    sampleHolder.acceptFrom(() -> {
                        throw new IllegalArgumentException("holder");
                    });
                });

                it("maps exception to a particular value", () ->
                    expect(sampleHolder.whenException().thenSet(exception -> "ZZZ").get()).toEqual("ZZZ"));

                it("returns true for hasException()", () ->
                    expect(sampleHolder.hasException()).toBeTrue());

                it("returns flase for isEmpty()", () ->
                    expect(sampleHolder.isEmpty()).toBeFalse());

                it("returns true for hasException() with thrown exception, false for any other", () -> {
                    expect(sampleHolder.hasException(IllegalArgumentException.class)).toBeTrue();
                    expect(sampleHolder.hasException(RuntimeException.class)).toBeTrue();
                    expect(sampleHolder.hasException(InterruptedException.class)).toBeFalse();
                });

                it("does not throw when ensureException() with thrown", () -> {
                    try {
                        sampleHolder.ensureException(IllegalArgumentException.class);
                    }
                    catch (final Exception exception) {
                        fail("Unexpected exception");
                    }
                });

                it("throws for ensureException(false)", () -> {
                    try {
                        sampleHolder.ensureException(false);
                        fail("No expected exception");
                    }
                    catch (final Exception exception) {
                        expect(exception.getClass()).toEqual(IllegalStateException.class);
                    }
                });

                it("does not throw for ensureException(true)", () -> {
                    try {
                        sampleHolder.ensureException(true);
                    }
                    catch (final Exception exception) {
                        fail("Unexpected exception");
                    }
                });

                it("throws illegal state when ensureException() with another exception", () -> {
                    try {
                        sampleHolder.ensureException(IllegalStateException.class);
                        fail("No expected exception");
                    }
                    catch (final Exception exception) {
                        expect(exception.getClass()).toEqual(IllegalStateException.class);
                        expect(exception.getCause()).toBeNotNull();
                        expect(exception.getCause().getClass()).toEqual(IllegalArgumentException.class);
                        expect(exception.getCause().getMessage()).toEqual("holder");
                    }
                });

                it("throws illegal state with exception when asked for value", () -> {
                    try {
                        sampleHolder.get();
                        fail("No expected exception");
                    }
                    catch (final Exception exception) {
                        expect(exception.getClass()).toEqual(IllegalStateException.class);
                        expect(exception.getCause()).toBeNotNull();
                        expect(exception.getCause().getClass()).toEqual(IllegalArgumentException.class);
                        expect(exception.getCause().getMessage()).toEqual("holder");
                    }
                });

                describe("for raiseException()", () -> {
                    it("does not throw when converter returned null ", () -> {
                        final boolean processed[] = {false};

                        sampleHolder.whenException().thenRaise(exception -> {
                            processed[0] = true;
                            return null;
                        });

                        expect(processed[0]).toBeTrue();
                    });

                    it("does not invoke for not matching exception class", () -> {
                        sampleHolder.whenException(MissingResourceException.class).thenRaise(exception -> {
                            fail("Unexpected block invocation");
                            return null;
                        });
                    });

                    it("throws converted exeception", () -> {
                        try {
                            sampleHolder.whenException().thenRaise(exception -> new IllegalStateException("WRAP", exception));
                            fail("No expected exception");
                        }
                        catch (final IllegalStateException exception) {
                            expect(exception.getMessage()).toEqual("WRAP");
                            expect(exception.getCause().getMessage()).toEqual("holder");
                        }
                    });
                });
            });

            describe("when created with a value", () -> {
                beforeEach(() -> {
                    sampleHolder = new Holder<>("abc");
                });

                it("returns false for isEmpty()", () -> expect(sampleHolder.isEmpty()).toBeFalse());
                it("returns true for hasValue()", () -> expect(sampleHolder.hasValue()).toBeTrue());
                it("returns false for hasException()", () -> expect(sampleHolder.hasException()).toBeFalse());
                it("returns same value for get()", () -> expect(sampleHolder.get()).toEqual("abc"));

                it("does not tap into null value", () -> {
                    hold((String) null).tap(sampleHolder::set);
                    expect(sampleHolder.get()).toEqual("abc");
                });

                it("throws for ensureValue(false)", () -> {
                    try {
                        sampleHolder.ensureValue(false);
                        fail("No expected exception");
                    }
                    catch (final IllegalStateException exception) {}
                });

                it("does not throw for ensureValue(true)", () -> {
                    try {
                        sampleHolder.ensureValue(true);
                    }
                    catch (final Exception exception) {
                        fail("Unexpected exception");
                    }
                });

                it("throws when acceptFrom() throws", () -> {
                    try {
                        sampleHolder.acceptFrom(() -> {
                            throw new Exception("LEAK");
                        })
                            .whenException().thenRaiseForcingRuntime();

                        fail("No excpected exception");
                    }
                    catch (final RuntimeException exception) {}
                });

                it("does not throw when acceptFrom() does not throw", () -> {
                    try {
                        sampleHolder.acceptFrom(() -> "zzz")
                            .whenException().thenRaiseForcingRuntime();
                    }
                    catch (final Exception exception) {
                        fail("Unexcpected exception");
                    }
                });

                it("does not invokes lambda for whenEmpty()", () -> {
                    final boolean processed[] = {false};

                    sampleHolder.whenEmpty().thenRun(() -> processed[0] = true);
                    expect(processed[0]).toBeFalse();
                });

                it("maps value to upper case", () ->
                    expect(string(sampleHolder.map(value -> value.toUpperCase()))).toEqual("ABC"));

                it("returns holder value for orElse()", () ->
                    expect(sampleHolder.orElse("UUU")).toEqual("abc"));

                it("returns holder value for orElseGet()", () ->
                    expect(sampleHolder.orElseGet(() -> "UUU")).toEqual("abc"));
            });

            it("keeps value when filter returns true", () ->
                expect(hold("hey").filter(s -> true).get()).toEqual("hey"));

            it("clears holder when filter returns false", () ->
                expect(hold("hey").filter(s -> false).isEmpty()).toBeTrue());

            describe("when used for exception stopping", () -> {
                it("returns proper value when no exception", () -> {
                    expect(holdFrom(() -> "zzz").get()).toEqual("zzz");
                });

                describe("when exception raised before returning value", () -> {
                    final Object[] depot = {"---", "---", "---", "---", "---"};

                    beforeEach(() -> {
                        holdFrom(() -> {
                            if (depot.length != 0) {
                                throw new RuntimeException("Must not leak");
                            }

                            return "uuu";
                        })
                        .whenException().thenProcess(exception -> depot[0] = exception.getMessage())
                        .whenEmpty().thenRun(() -> depot[1] = "Set empty")
                        .filter(value -> {
                            depot[2] = value;
                            return true;
                        })
                        .tap(value -> depot[3] = value)
                        .map(value -> depot[4] = value);
                    });

                    it("invokes whenException() with raised exception", () -> expect(depot[0]).toEqual("Must not leak"));
                    it("invokes whenEmpty()", () -> expect(depot[1]).toEqual("Set empty"));
                    it("does not invoke filter()", () -> expect(depot[2]).toEqual("---"));
                    it("does not invoke tap()", () -> expect(depot[3]).toEqual("---"));
                    it("does not invoke map()", () -> expect(depot[4]).toEqual("---"));
                });

                describe("when exception raised without returning value", () -> {
                    final Object[] depot = {"---", "---", "---", "---", "---"};

                    beforeEach(() -> {
                        holdFrom(() -> {
                            throw new RuntimeException("Must not leak");
                        })
                        .whenException().thenProcess(exception -> depot[0] = exception.getMessage())
                        .whenEmpty().thenRun(() -> depot[1] = "Set empty")
                        .filter(value -> {
                            depot[2] = value;
                            return true;
                        })
                        .tap(value -> depot[3] = value)
                        .map(value -> depot[4] = value);
                    });

                    it("invokes whenException() with raised exception", () -> expect(depot[0]).toEqual("Must not leak"));
                    it("invokes whenEmpty()", () -> expect(depot[1]).toEqual("Set empty"));
                    it("does not invoke filter()", () -> expect(depot[2]).toEqual("---"));
                    it("does not invoke tap()", () -> expect(depot[3]).toEqual("---"));
                    it("does not invoke map()", () -> expect(depot[4]).toEqual("---"));
                });
            });

            it("stays empty invoing runnalbe when not throwing", () ->
                expect(holdFrom(() -> {}).isEmpty()).toBeTrue());

            it("gets exception invoking throwing runnalbe", () -> {
                try {
                    holdFrom(() -> {
                        throw new IllegalArgumentException("BANG");
                    })
                    .whenException().thenProcess(exception -> {
                        throw new IllegalStateException("WRAP", exception);
                    });

                    fail("No expected exception");
                }
                catch (final Exception exception) {
                    expect(exception.getClass()).toEqual(IllegalStateException.class);
                    expect(exception.getMessage()).toEqual("WRAP");
                    expect(exception.getCause().getClass()).toEqual(IllegalArgumentException.class);
                    expect(exception.getCause().getMessage()).toEqual("BANG");
                }
            });

            describe("for whenException() with exception class", () -> {
                it("not invoking when no exception", () -> {
                    final boolean processed[] = {false};

                    holdFrom(() -> "hello")
                        .whenException(IllegalArgumentException.class).thenProcess(exception -> processed[0] = true)
                        .whenException(IllegalArgumentException.class).thenSet(exception -> exception.getMessage())
                        .whenEmpty().thenRun(() -> processed[0] = true)
                        .tap(value -> expect(value).toEqual("hello"));

                    expect(processed[0]).toBeFalse();
                });

                it("not invoking when wrong exception", () -> {
                    final boolean processed[] = {false};

                    holdFrom(() -> {
                        throw new IllegalStateException("error");
                    })
                        .whenException(IllegalArgumentException.class).thenProcess(exception -> processed[0] = true)
                        .whenException(IllegalArgumentException.class).thenSet(exception -> exception.getMessage())
                        .whenException(IllegalStateException.class).thenSet(exception -> exception.getMessage())
                        .whenEmpty().thenSet("filled-up")
                        .tap(value -> expect(value).toEqual("error"));

                    expect(processed[0]).toBeFalse();
                });

                it("invokes for same exception", () -> {
                    final boolean processed[] = {false};

                    holdFrom(() -> {
                        throw new IllegalArgumentException("error-to-map");
                    })
                        .whenException(IllegalArgumentException.class).thenProcess(exception -> processed[0] = true)
                        .whenException().thenSet(exception -> exception.getMessage())
                        .tap(value -> expect(value).toEqual("error-to-map"))
                        .filter(value -> false)
                        .whenEmpty().thenSet("filled-up")
                        .tap(value -> expect(value).toEqual("filled-up"));

                    expect(processed[0]).toBeTrue();
                });
            });
        });
    }

    private String string(final String string) {
        return string;
    }
}
