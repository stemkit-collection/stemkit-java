/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.sk.i18n.exception;

public class FallbackOverrideMessageDepotException extends MessageDepotException {
    private static final long serialVersionUID = 0;

    public FallbackOverrideMessageDepotException(final String code, final String fallback, final String existingFallback) {
        super("Replacing existing fallback", code, fallback, existingFallback);
    }
}
