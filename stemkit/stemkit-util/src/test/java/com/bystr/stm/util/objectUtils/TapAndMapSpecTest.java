/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm.util.objectUtils;

import static com.bystr.stm.util.ObjectUtils.filter;
import static com.bystr.stm.util.ObjectUtils.map;
import static com.bystr.stm.util.ObjectUtils.mapNullable;
import static com.bystr.stm.util.ObjectUtils.tap;
import static com.bystr.stm.util.ObjectUtils.tapNullable;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.describe;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.it;

import org.junit.runner.RunWith;

import com.bystr.stm.bdd.BDDSpecRunner;
import com.bystr.stm.util.ObjectUtils;

@RunWith(BDDSpecRunner.class)
public class TapAndMapSpecTest {
    {
        describe(ObjectUtils.class.getName(), () -> {
            it("maps non-null values", () -> {
                expect(string(map("abc", String::toUpperCase))).toEqual("ABC");
                expect(string(map("DDD", String::toLowerCase))).toEqual("ddd");
            });

            it("maps from the outer scope", () -> {
                expect(map("zzz", () -> "hoho")).toEqual("hoho");
                expect(map(null, () -> "hoho")).toBeNull();
            });

            it("taps preserving value", () -> {
                final String[] holder = {"---"};

                expect(tap("cba", s -> holder[0] = s.toUpperCase())).toEqual("cba");
                expect(holder[0]).toEqual("CBA");
            });

            it("taps without reference preserving value", () -> {
                final String[] holder = {"---"};

                expect(tap("cba", () -> holder[0] = "got-here")).toEqual("cba");
                expect(holder[0]).toEqual("got-here");
            });

            it("does not tap/map/filter null values", () -> {
                final String[] holder = {"---"};

                expect(tap(string(null), () -> holder[0] = "got-here")).toBeNull();
                expect(holder[0]).toEqual("---");

                expect(tap(string(null), s -> holder[0] = "got-here")).toBeNull();
                expect(holder[0]).toEqual("---");

                expect(string(map(string(null), s -> holder[0] = "got-here"))).toBeNull();
                expect(holder[0]).toEqual("---");

                final boolean[] processed = {false};
                expect(filter(string(null), s -> processed[0] = true)).toBeNull();
                expect(processed[0]).toEqual(false);
            });

            it("taps nullable with null value", () -> {
                final boolean[] processed = {false};

                final String value = tapNullable(string(null), s -> {
                    processed[0] = true;

                    expect(s).toBeNotNull();
                    expect(s.isPresent()).toBeFalse();
                });

                expect(processed[0]).toBeTrue();
                expect(value).toBeNull();
            });

            it("taps nullable with non-null value", () -> {
                final boolean[] processed = {false};

                final String value = tapNullable("hello", s -> {
                    processed[0] = true;

                    expect(s).toBeNotNull();
                    expect(s.isPresent()).toBeTrue();
                    expect(s.get()).toEqual("hello");
                });

                expect(processed[0]).toBeTrue();
                expect(value).toEqual("hello");
            });

            it("maps nullable null and non-null", () -> {
                expect(string(mapNullable(string(null), s -> s.orElse("other")))).toEqual("other");
                expect(string(mapNullable(string(null), s -> s.orElse(null)))).toBeNull();
                expect(string(mapNullable(null, s -> "ddd"))).toEqual("ddd");
                expect(string(mapNullable("abc", s -> null))).toBeNull();
                expect(string(mapNullable("zzz", s -> s.orElse("other").toUpperCase()))).toEqual("ZZZ");
            });

            describe("filtering values", () -> {
                it("leaves value when predicate is true", () -> {
                    expect(filter("abc", s -> true)).toEqual("abc");
                });

                it("removes value when predicate is false", () -> {
                    expect(filter("abc", s -> false)).toBeNull();
                });
            });
        });
    }

    private static String string(final String string) {
        return string;
    }
}
