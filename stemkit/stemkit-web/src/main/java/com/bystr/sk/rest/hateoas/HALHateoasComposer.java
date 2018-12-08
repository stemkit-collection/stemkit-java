/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.sk.rest.hateoas;

public class HALHateoasComposer<T> extends AbstractHateoasComposer<T> {
    public HALHateoasComposer(final Class<T> targetClass) {
        super(targetClass);
    }
}
