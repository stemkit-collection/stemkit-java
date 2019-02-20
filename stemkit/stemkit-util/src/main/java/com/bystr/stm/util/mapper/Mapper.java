/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm.util.mapper;

import com.fasterxml.jackson.databind.JsonNode;

public interface Mapper<T> {
    String serialize(final T object);

    T compose(final String jsonText);
    T compose(final JsonNode jsonNode);
}
