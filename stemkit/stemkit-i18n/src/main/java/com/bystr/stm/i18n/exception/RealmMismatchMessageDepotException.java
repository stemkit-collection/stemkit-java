/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm.i18n.exception;

public class RealmMismatchMessageDepotException extends MessageDepotException {
    private static final long serialVersionUID = 0;

    public RealmMismatchMessageDepotException(final String realm, final String name) {
        super("Not under realm", realm, name);
    }
}
