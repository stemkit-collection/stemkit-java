/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

/**
 * JUnit extension for BDD (Behavior Driven Development) style unit tests (specs).
 * <p>
 * It is based on <a href="https://github.com/mscharhag/oleaster">Oleaster facility</a>
 * and provides <a href="https://www.mscharhag.com/java/oleaster-jasmine-junit-tests">
 * an alternative approach of writing JUnit tests</a>. This approach is very similar in
 * syntax to that of JavaScript provided by Jasmine framework. It extensively utilizes
 * Java's lambda facility making the tests very expressive without big overhead and the
 * limitations of the original JUnit approach.
 * <p>
 * This facility has also integration with Mocking mocking framework and Spring dependency
 * injection framework.
 * <p>
 * Here's an example of a spec definition:
 * <pre>
 * {@code @RunWith(BDDSpecRunner.class)}
 * public class SimpleObjectSpecTest {
 *     {
 *         describe(Object.class.getName(), () {@code ->} {
 *             beforeEach(() {@code ->} {});
 *             afterEach(() {@code ->} {});
 *
 *             it("states the obvious", () {@code ->} expect(true).toBeTrue());
 *
 *             describe("under certain condition", () {@code ->} {
 *                 beforeEach(() {@code ->} setupCertainCondition());
 *
 *                 it("ensures certain state", () {@code ->} expect(certainState()).toBeTrue());
 *             });
 *         });
 *     }
 *
 *     private void setupCertainCondition() {
 *         return;
 *     }
 *
 *     private boolean certainState() {
 *         return true;
 *     }
 * }
 * </pre>
 * The above specification generates the following reports:
 * <pre>
 * v java.lang.Object, it states the obvious - com.bystr.sk.bdd.SimpleObjectTest (0.000s)
 * v java.lang.Object, under certain condition, it ensures certain state - com.bystr.sk.bdd.SimpleObjectTest (0.000s)
 * </pre>
 * Those reports will be as good as the author is creative in messages for
 * {@code describe()} and {@code it()}.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
 *
 * @see com.bystr.sk.bdd.BDDSpecRunner
*/
package com.bystr.sk.bdd;
