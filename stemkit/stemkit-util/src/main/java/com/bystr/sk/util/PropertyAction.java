/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.sk.util;

import static java.util.stream.Collectors.joining;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

// {@link PropertyActionTest}
public class PropertyAction {
    public static boolean perform(final String name, final Consumer<PropertyAction> consumer) {
        PropertyAction selector = new PropertyAction(name.split("[-:._]+"));
        consumer.accept(selector);

        return selector.product();
    }

    PropertyAction(final String[] strings) {
        _product = false;

        if (retrieve("property", makePropertyName(strings), System::getProperty)) {
            return;
        }

        if (retrieve("environment", makeEnvironmentName(strings), System::getenv)) {
            return;
        }
    }

    public String name() {
        return _name;
    }

    public boolean product() {
        return _product;
    }

    public void produce(final boolean product) {
        _product = product;
    }

    private boolean retrieve(final String source, final String name, final Function<String, String> retriever) {
        final String value = retriever.apply(name);

        if (value == null) {
            return false;
        }

        _source = source;
        _value = value.trim().toLowerCase();
        _name = name;

        if (_value.isEmpty()) {
            _value = null;
        }

        return true;
    }

    public void whenTrue(final Runnable runnable) {
        if (_value != null && _value.matches("^(true)|(yes)$")) {
            runnable.run();
        }
    }

    public void whenFalse(final Runnable runnable) {
        if (_value == null || _value.matches("^(false)|(no)$")) {
            runnable.run();
        }
    }

    public void whenString(final Consumer<String> consumer) {
        if (_value != null) {
            consumer.accept(_value);
        }
    }

    public void whenNone(final Runnable runnable) {
        if (_value == null) {
            runnable.run();
        }
    }

    public String source() {
        return _source;
    }

    public String value() {
        return _value;
    }

    private String makePropertyName(final String[] strings) {
        return Stream.of(strings).map(String::toLowerCase).collect(joining("."));
    }

    private String makeEnvironmentName(final String[] strings) {
        return Stream.of(strings).map(String::toUpperCase).collect(joining("_"));
    }

    private String _source;
    private String _value;
    private String _name;

    private boolean _product;
}
