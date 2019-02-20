/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm.rest.hateoas;

public class HateoasFormatException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public HateoasFormatException() {
        super("Wrong HATEOAS data");
    }
}
