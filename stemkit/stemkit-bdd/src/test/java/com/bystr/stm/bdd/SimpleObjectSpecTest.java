/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm.bdd;

import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.afterEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.beforeEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.describe;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.it;

import org.junit.runner.RunWith;

@RunWith(BDDSpecRunner.class)
public class SimpleObjectSpecTest {
    {
        describe(Object.class.getSimpleName(), () -> {
            beforeEach(() -> {});
            afterEach(() -> {});

            it("states the obvious", () -> expect(true).toBeTrue());

            describe("under certain condition", () -> {
                beforeEach(() -> setupCertainCondition());

                it("ensures certain state", () -> expect(certainState()).toBeTrue());
            });
        });

        // The above specification generates the following reports:
        //     v java.lang.Object, it states the obvious - com.bystr.stm.bdd.SimpleObjectTest (0.000s)
        //     v java.lang.Object, under certain condition, it ensures certain state - com.bystr.stm.bdd.SimpleObjectTest (0.000s)
        //
        // Those reports will be as good as the author is creative in messages for describe() and it().
    }

    private void setupCertainCondition() {
        return;
    }

    private boolean certainState() {
        return true;
    }
}
