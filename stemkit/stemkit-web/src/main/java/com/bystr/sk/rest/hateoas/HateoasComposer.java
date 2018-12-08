/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.sk.rest.hateoas;

import java.util.Collection;

import com.fasterxml.jackson.databind.JsonNode;

public interface HateoasComposer<T> {
    T composeSingle(final JsonNode jsonNode);
    T composeSingle(final String jsonText);

    Collection<T> composeCollection(final JsonNode jsonNode);
    Collection<T> composeCollection(final String jsonText);
}
