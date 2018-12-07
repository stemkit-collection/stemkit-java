package com.bystr.sk.util.identifiableStringsBundle;

import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.beforeEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.describe;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.it;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.runner.RunWith;

import com.bystr.sk.bdd.BDDSpecRunner;
import com.bystr.sk.util.Holder;
import com.bystr.sk.util.IdentifiableStringsBundle;
import com.bystr.sk.util.mapper.JsonMapper;
import com.bystr.sk.util.mapper.Mapper;
import com.fasterxml.jackson.core.type.TypeReference;

@RunWith(BDDSpecRunner.class)
public class MasterSpecTest {
    private final Mapper<IdentifiableStringsBundle> mapper = new JsonMapper<>(IdentifiableStringsBundle.class);
    private final Holder<IdentifiableStringsBundle> holder = new Holder<>();

    {
        describe(IdentifiableStringsBundle.class.getName(), () -> {
            describe("when created with id", () -> {
                beforeEach(() -> {
                    holder.set(new IdentifiableStringsBundle("abc"));
                });

                it("returns id at creation", () -> expect(holder.get().getId()).toEqual("abc"));
                it("returns empty set of strings", () -> expect(holder.get().getStrings().isEmpty()).toBeTrue());

                it("serializes into proper JSON", () ->
                    expect(mapper.serialize(holder.get()))
                        .toEqual("{\"id\":\"abc\",\"strings\":[]}"));
            });

            describe("when created with id and strings", () -> {
                beforeEach(() -> {
                    holder.set(new IdentifiableStringsBundle("n3", Stream.of("a", "b", "c").collect(toSet())));
                });

                it("returns id at creation", () -> expect(holder.get().getId()).toEqual("n3"));
                it("returns number of strings at creation", () -> expect(holder.get().getStrings().size()).toEqual(3));

                it("serializes into proper JSON", () ->
                    expect(mapper.serialize(holder.get()))
                        .toEqual("{\"id\":\"n3\",\"strings\":[\"a\",\"b\",\"c\"]}"));
            });

            describe("when making instance from JSON with id only", () -> {
                beforeEach(() -> {
                    holder.set(mapper.compose("{\"id\":\"zzz\"}"));
                });

                it("returns proper id", () -> expect(holder.get().getId()).toEqual("zzz"));
                it("returns empty set of strings", () -> expect(holder.get().getStrings().isEmpty()).toBeTrue());
            });

            describe("when making instance from JSON with all data", () -> {
                beforeEach(() -> {
                    holder.set(mapper.compose("{\"id\":\"uuu\",\"strings\":[\"aaa\",\"bbb\"]}"));
                });

                it("returns proper id", () -> expect(holder.get().getId()).toEqual("uuu"));
                it("returns proper number of strings", () -> expect(holder.get().getStrings().size()).toEqual(2));
            });

            it("checks equal for bundles with same data", () -> {
                final IdentifiableStringsBundle b1 = new IdentifiableStringsBundle("aaa", makeSet("zzz", "UUU"));
                final IdentifiableStringsBundle b2 = new IdentifiableStringsBundle("aaa", makeSet("UUU", "zzz"));

                expect(b1.equals(b2)).toBeTrue();
                expect(Arrays.asList(b1).equals(Arrays.asList(b2))).toBeTrue();

                expect(b1.hashCode() == b2.hashCode()).toBeTrue();
            });

            it("checks not equal for bundles with diffent data", () -> {
                final IdentifiableStringsBundle b1 = new IdentifiableStringsBundle("aaa", makeSet("zzz", "UUU"));
                final IdentifiableStringsBundle b2 = new IdentifiableStringsBundle("aaa", makeSet("ccc", "BBB"));

                expect(b1.equals(b2)).toBeFalse();
                expect(Arrays.asList(b1).equals(Arrays.asList(b2))).toBeFalse();

                expect(b1.hashCode() == b2.hashCode()).toBeFalse();
            });

            it("serializes from and composes into a set of bundles", () -> {
                final IdentifiableStringsBundle[] bundles = {
                    new IdentifiableStringsBundle("b1", makeSet("a", "b")),
                    new IdentifiableStringsBundle("b2", makeSet("c", "d")),
                };

                final JsonMapper<List<IdentifiableStringsBundle>> mapper = new JsonMapper<>(new TypeReference<List<IdentifiableStringsBundle>>() {});
                final String dump = mapper.serialize(Stream.of(bundles).collect(toList()));

                expect(dump).toStartWith("[{");
                expect(dump).toEndWith("}]");

                final List<IdentifiableStringsBundle> restored = mapper.compose(dump);
                expect(restored.size()).toEqual(2);

                expect(restored.get(0).getId()).toEqual("b1");
                expect(restored.get(0).getStrings()).toEqual(makeSet("b", "a"));

                expect(restored.get(1).getId()).toEqual("b2");
                expect(restored.get(1).getStrings()).toEqual(makeSet("d", "c"));
            });
        });
    }

    private static Set<String> makeSet(final String...strings) {
        return Stream.of(strings).collect(toSet());
    }
}
