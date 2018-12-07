package com.bystr.sk.i18n.depot;

import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.beforeEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.describe;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.it;

import org.junit.runner.RunWith;

import com.bystr.sk.i18n.StationaryMessageBundleFactory;
import com.bystr.sk.i18n.depot.i18n.TopLevelMessageDepot;
import com.bystr.sk.bdd.BDDSpecRunner;

@RunWith(BDDSpecRunner.class)
public class TopLevelSpecTest {
    TopLevelMessageDepot depot;

    {
        describe(TopLevelMessageDepot.class.getSimpleName(), () -> {
            beforeEach(() -> {
                depot = new TopLevelMessageDepot(new StationaryMessageBundleFactory());
            });

            it("has empty namespace", () -> expect(depot.namespace()).toEqual(""));
            it("has root bundle name", () -> expect(depot.bundleName()).toMatch("messages[\\\\/]root"));

            describe("with default setup", () -> {
                it("produces messages without code prefix", () -> {
                    expect(depot.m1()).toEqual("Message 1");
                    expect(depot.m2()).toEqual("Message 2");
                });
            });

            describe("with useCodePrefix(true) setup", () -> {
                beforeEach(() -> depot.useCodePrefix(true));

                it("produces messages without code prefix", () -> {
                    expect(depot.m1()).toEqual("[m1] Message 1");
                    expect(depot.m2()).toEqual("[m2] Message 2");
                });
            });
        });
    }
}
