package com.bystr.sk.util;

import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.afterEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.beforeEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.describe;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.it;

import org.junit.runner.RunWith;

import com.bystr.sk.bdd.BDDSpecRunner;
import com.bystr.sk.util.PropertyAction;

@RunWith(BDDSpecRunner.class)

// {@link PropertyAction}
public class PropertyActionSpecTest {
    private boolean performed;
    private boolean trueAction;
    private boolean falseAction;
    private boolean noneAction;
    private String stringValue;

    {
        describe("Property action", () -> {
            beforeEach(() -> {
                System.clearProperty("sk.perform.action");
                System.clearProperty("home");

                performed = false;
            });

            afterEach(() -> {
                expect(performed).toBeTrue();
            });

            it("should recognize environment", () -> {
                PropertyAction.perform("home", selector -> {
                    performed = true;

                    expect(selector.source()).toEqual("environment");
                    expect(selector.name()).toEqual("HOME");
                    expect(selector.value()).toEqual(System.getenv("HOME").toLowerCase());
                });
            });

            it("should recognize property", () -> {
                System.setProperty("uuu.zzz", "ABC");

                PropertyAction.perform("UUU-ZZz", selector -> {
                    performed = true;

                    expect(selector.source()).toEqual("property");
                    expect(selector.name()).toEqual("uuu.zzz");
                    expect(selector.value()).toEqual("abc");
                });
            });

            it("should process properties before environment", () -> {
                System.setProperty("home", "hehehe");

                PropertyAction.perform("HomE", selector -> {
                    performed = true;

                    expect(selector.source()).toEqual("property");
                    expect(selector.name()).toEqual("home");
                    expect(selector.value()).toEqual("hehehe");
                });
            });

            describe("with action markers", () -> {
                beforeEach(() -> {
                    trueAction = false;
                    falseAction = false;
                    noneAction = false;
                });

                it("should not invoke on gabberish", () -> {
                    performWith("trueyesfalsennone");

                    expect(trueAction).toBeFalse();
                    expect(falseAction).toBeFalse();
                    expect(noneAction).toBeFalse();
                });


                it("should invoke true action on true", () -> {
                    performWith("true");

                    expect(trueAction).toBeTrue();
                    expect(falseAction).toBeFalse();
                    expect(noneAction).toBeFalse();
                });

                it("should invoke true action on true with capitals and spaces", () -> {
                    performWith("   tRUe   ");

                    expect(trueAction).toBeTrue();
                    expect(falseAction).toBeFalse();
                    expect(noneAction).toBeFalse();
                });

                it("should invoke true action on capitalized yes", () -> {
                    performWith("Yes");

                    expect(trueAction).toBeTrue();
                    expect(falseAction).toBeFalse();
                    expect(noneAction).toBeFalse();
                });

                it("should invoke false action on capitalized false", () -> {
                    performWith("False");

                    expect(trueAction).toBeFalse();
                    expect(falseAction).toBeTrue();
                    expect(noneAction).toBeFalse();
                });

                it("should invoke false action on no", () -> {
                    performWith("no");

                    expect(trueAction).toBeFalse();
                    expect(falseAction).toBeTrue();
                    expect(noneAction).toBeFalse();
                });

                it("should invoke false and none actions on null", () -> {
                    performWith(null);

                    expect(trueAction).toBeFalse();
                    expect(falseAction).toBeTrue();
                    expect(noneAction).toBeTrue();
                });

                it("should invoke false and none actions on empty", () -> {
                    performWith("     ");

                    expect(trueAction).toBeFalse();
                    expect(falseAction).toBeTrue();
                    expect(noneAction).toBeTrue();
                });
            });

            describe("for string values", () -> {
                beforeEach(() -> {
                    stringValue = null;
                });

                it("should invoke string action with trimmed downcased on non null", () -> {
                    performWith("  Aaa bBB  ");
                    expect(stringValue).toEqual("aaa bbb");
                });

                it("should not invoke string action on null", () -> {
                    performWith(null);
                    expect(stringValue).toBeNull();
                });

                it("should not_invoke_string_action_on_empty", () -> {
                    performWith("       ");
                    expect(stringValue).toBeNull();
                });
            });

            describe("for returned values", () -> {
                it("should return false by default", () -> {
                    expect(
                        PropertyAction.perform("aaa.bbb.ccc", selector -> {
                            performed = true;
                        })
                    ).toBeFalse();
                });

                it("should return true if specified", () -> {
                    expect(
                        PropertyAction.perform("aaa.bbb.ccc", selector -> {
                            performed = true;
                            selector.produce(true);
                        })
                    ).toBeTrue();
                });
            });
        });
    }

    private void performWith(final String content) {
        if (content != null) {
            System.setProperty("sk.perform.action", content);
        }

        PropertyAction.perform("sk-perform-action", selector -> {
            this.performed = true;

            selector.whenTrue(() -> {
                this.trueAction = true;
            });

            selector.whenFalse(() -> {
                this.falseAction = true;
            });

            selector.whenNone(() -> {
                this.noneAction = true;
            });

            selector.whenString(value -> {
                this.stringValue = value;
            });
        });
    }
}
