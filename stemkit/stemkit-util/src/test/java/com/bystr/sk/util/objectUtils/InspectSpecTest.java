package com.bystr.sk.util.objectUtils;

import static com.bystr.sk.util.ObjectUtils.inspect;
import static com.bystr.sk.util.ObjectUtils.makeMap;
import static com.bystr.sk.util.ObjectUtils.makeSet;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.beforeEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.describe;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.it;

import java.util.UUID;

import org.junit.runner.RunWith;

import com.bystr.sk.bdd.BDDSpecRunner;
import com.bystr.sk.util.Holder;
import com.bystr.sk.util.ObjectUtils;

@RunWith(BDDSpecRunner.class)
public class InspectSpecTest {
    {
        describe(ObjectUtils.class.getName(), () -> {
            describe("inspects a string", () -> {
                it("being null", () ->
                    expect(inspect(string(null)))
                        .toEqual("<null>"));

                it("being empty", () ->
                    expect(inspect(""))
                        .toEqual("\"\""));

                it("containing quotes", () ->
                    expect(inspect("aaa\"bbb\"ccc"))
                        .toEqual("\"aaa\\\"bbb\\\"ccc\""));

                it("containing newlines and returns", () ->
                    expect(inspect("aaa\nbbb\rccc"))
                        .toEqual("\"aaa\\nbbb\\rccc\""));
            });

            describe("inspects a set", () -> {
                it("being empty", () ->
                    expect(inspect(makeSet()))
                        .toEqual("[]"));

                it("having one string", () ->
                    expect(inspect(makeSet("zzz")))
                        .toEqual("[\"zzz\"]"));

                it("having two integers", () ->
                    expect(inspect(makeSet(15, 34)))
                        .toEqual("[2: 34, 15]"));
            });

            describe("inspects a map", () -> {
                it("being empty", () ->
                    expect(inspect(makeMap().map))
                        .toEqual("{}"));

                it("having one entry", () ->
                    expect(inspect(makeMap().put(5, "zzz").map))
                        .toEqual("{5=>\"zzz\"}"));

                it("having two entries", () ->
                    expect(inspect(makeMap().put("a1", "zzz").put("a2", 15).map))
                        .toEqual("{2: \"a1\"=>\"zzz\", \"a2\"=>15}"));
            });

            describe("inspects an UUID", () -> {
                final Holder<UUID> uuidHolder = new Holder<>();

                beforeEach(() -> uuidHolder.set(UUID.randomUUID()));

                it("being null", () ->
                    expect(inspect(uuidHolder.clear().get()))
                        .toEqual("<null>"));

                it("being random", () ->
                    expect(inspect(uuidHolder.get()))
                        .toEqual("<UUID:" + uuidHolder.get().toString() + ">"));
            });
        });
    }

    private static String string(final String string) {
        return string;
    }
}
