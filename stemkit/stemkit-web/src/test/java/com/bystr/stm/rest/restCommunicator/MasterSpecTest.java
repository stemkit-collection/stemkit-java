/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm.rest.restCommunicator;

import static com.bystr.stm.bdd.BDDSpecRunner.strictMock;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.beforeEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.describe;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.it;

import org.junit.runner.RunWith;
import org.springframework.web.client.RestOperations;

import com.bystr.stm.bdd.BDDSpecRunner;
import com.bystr.stm.rest.RestCommunicator;
import com.bystr.stm.util.Holder;

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
