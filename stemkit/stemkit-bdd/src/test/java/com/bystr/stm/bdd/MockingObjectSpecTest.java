/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm.bdd;

import static com.bystr.stm.bdd.BDDSpecRunner.initMocks;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.afterEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.beforeEach;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.describe;
import static com.mscharhag.oleaster.runner.StaticRunnerSupport.it;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(BDDSpecRunner.class)
public class MockingObjectSpecTest {
    @Mock List<String> mockList;
    @Mock Set<String> mockSet;

    UUID uuid;

    {
        describe("Object.class.getName()", () -> {
            beforeEach(() -> initMocks(this));

            beforeEach(() -> {
                uuid = UUID.randomUUID();

                expect(mockList).toBeNotNull();
                expect(mockSet).toBeNotNull();

                when(mockList.add(any())).thenReturn(false);
                when(mockSet.isEmpty()).thenReturn(true);
            });

            describe("in cetrain cases", () -> {
                beforeEach (() -> {});
                afterEach(() -> {});

                it ("calculates complex equasions", () -> expect(2 + 2).toEqual(4));
                it ("ensures something important", () -> expect(uuid.toString().length()).toBeGreaterThan(0));
            });

            describe("with local strings", () -> {
                final String[] subject = {null};

                beforeEach (() -> {
                    subject[0] = null;
                });

                afterEach(() -> {
                    expect(subject[0]).toBeNotNull();
                    verify(mockList, atLeastOnce()).add(any(String.class));
                });

                it ("adds a string", () -> {
                    subject[0] = "s1";
                    mockList.add(subject[0]);
                });

                it ("and another", () -> {
                    subject[0] = "s2";
                    mockList.add(subject[0]);
                });
            });
        });
    }
}
