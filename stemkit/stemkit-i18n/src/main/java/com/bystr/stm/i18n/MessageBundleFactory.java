/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm.i18n;

/**
 * Defines message bundle factory operations.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
public interface MessageBundleFactory {
    /**
     * Creates a message bundle.
     * <p>
     * @param bundleName [String]
     *     a character string to use for building Java resource bundle
     *     file name to load.
     * <p>
     * @return [{@link MessageBundle}]
     *     a created message bundle.
    */
    MessageBundle makeMessageBundle(final String bundleName);
}
