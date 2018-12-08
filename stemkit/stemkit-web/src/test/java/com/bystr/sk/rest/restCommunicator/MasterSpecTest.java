package com.bystr.sk.rest.restCommunicator;

import static com.bystr.sk.bdd.BDDSpecRunner.strictMock;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.beforeEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.describe;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.it;

import org.junit.runner.RunWith;
import org.springframework.web.client.RestOperations;

import com.bystr.sk.bdd.BDDSpecRunner;
import com.bystr.sk.rest.RestCommunicator;
import com.bystr.sk.util.Holder;

@RunWith(BDDSpecRunner.class)
public class MasterSpecTest {
    private MockRegistry mocks;
    private Holder<RestCommunicator<String>> subjectHolder = new Holder<>();

    {
        describe(RestCommunicator.class.getSimpleName(), () -> {
            beforeEach(() -> {
                subjectHolder.clear();
                mocks = new MockRegistry();
            });


            describe("composing from string", () -> {
                beforeEach(() -> {
                    subjectHolder
                        .acceptFrom(() -> new RestCommunicator<String>(mocks.restOperations, port -> ""))
                        .whenException().thenRaiseForcingRuntime();
                });

                it("must be available", () ->
                    expect(subjectHolder.hasValue()).toBeTrue());
            });
        });
    }

    private class MockRegistry {
        final RestOperations restOperations = strictMock(RestOperations.class);
    }
}
