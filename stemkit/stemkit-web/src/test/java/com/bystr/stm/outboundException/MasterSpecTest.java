/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm.outboundException;

import static com.bystr.stm.bdd.BDDSpecRunner.strictMock;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.beforeEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.describe;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.it;

import org.junit.runner.RunWith;

import com.bystr.stm.OutboundException;
import com.bystr.stm.bdd.BDDSpecRunner;
import com.bystr.stm.http.NotFoundStatusSource;
import com.bystr.stm.i18n.MessageDepot;
import com.bystr.stm.util.Holder;

@RunWith(BDDSpecRunner.class)
public class MasterSpecTest {
    private MockRegistry mocks;
    private Holder<OutboundException> subjectHolder = new Holder<>();

    private class SampleOutboundException extends OutboundException implements NotFoundStatusSource {
        private static final long serialVersionUID = 0;

        public SampleOutboundException(final MessageDepot messageDepot) {
            super(messageDepot, registry -> registry.reason("abc"));
        }
    }

    {
        describe(OutboundException.class.getSimpleName(), () -> {
            beforeEach(subjectHolder::clear);

            beforeEach(() -> {
                mocks = new MockRegistry();
            });

            describe("throwing exception", () -> {
                beforeEach(() -> {
                    subjectHolder.acceptFrom(() -> {
                        throw new SampleOutboundException(mocks.messageDepot);
                    });
                });

                it("must be available", () ->
                    expect(subjectHolder.hasException(OutboundException.class)).toBeTrue());
            });
        });
    }

    private class MockRegistry {
        final MessageDepot messageDepot = strictMock(MessageDepot.class);
    }
}
