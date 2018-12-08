/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.sk.rest.hateoas.halHateoasComposer;

import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.beforeEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.describe;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.it;

import org.junit.runner.RunWith;

import com.bystr.sk.bdd.BDDSpecRunner;
import com.bystr.sk.rest.hateoas.HALHateoasComposer;
import com.bystr.sk.rest.hateoas.HateoasComposer;
import com.bystr.sk.util.Holder;

@RunWith(BDDSpecRunner.class)
public class MasterSpecTest {
    private Holder<HateoasComposer<String>> subjectHolder = new Holder<>();

    {
        describe(HALHateoasComposer.class.getSimpleName(), () -> {
            beforeEach(subjectHolder::clear);

            describe("composing from string", () -> {
                beforeEach(() -> {
                    subjectHolder
                        .acceptFrom(() -> new HALHateoasComposer<>(String.class))
                        .whenException().thenRaiseForcingRuntime();
                });

                it("must be available", () ->
                    expect(subjectHolder.hasValue()).toBeTrue());
            });
        });
    }
}
