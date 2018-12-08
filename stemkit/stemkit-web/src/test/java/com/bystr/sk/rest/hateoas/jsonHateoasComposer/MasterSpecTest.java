package com.bystr.sk.rest.hateoas.jsonHateoasComposer;

import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.beforeEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.describe;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.it;

import org.junit.runner.RunWith;

import com.bystr.sk.bdd.BDDSpecRunner;
import com.bystr.sk.rest.hateoas.HateoasComposer;
import com.bystr.sk.rest.hateoas.JsonHateoasComposer;
import com.bystr.sk.util.Holder;

@RunWith(BDDSpecRunner.class)
public class MasterSpecTest {
    private Holder<HateoasComposer<String>> subjectHolder = new Holder<>();

    {
        describe(JsonHateoasComposer.class.getSimpleName(), () -> {
            beforeEach(subjectHolder::clear);

            describe("composing from string", () -> {
                beforeEach(() -> {
                    subjectHolder
                        .acceptFrom(() -> new JsonHateoasComposer<>(String.class))
                        .whenException().thenRaiseForcingRuntime();
                });

                it("must be available", () ->
                    expect(subjectHolder.hasValue()).toBeTrue());
            });
        });
    }
}
