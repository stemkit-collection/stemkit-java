/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm.rest.hateoas;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Collection;

import com.bystr.stm.util.mapper.JsonMapper;
import com.bystr.stm.util.mapper.Mapper;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class AbstractHateoasComposer<T> implements HateoasComposer<T> {
    private static final Mapper<JsonNode> jsonNodeMapper = new JsonMapper<>(JsonNode.class);
    private final Mapper<T> _targetMapper;

    public AbstractHateoasComposer(final Class<T> targetClass) {
        _targetMapper = new JsonMapper<>(targetClass);
    }

    protected JsonNode prepareForSingle(final JsonNode jsonNode) {
        return jsonNode;
    }

    @Override
    public T composeSingle(final JsonNode jsonNode) {
        return _targetMapper.compose(prepareForSingle(jsonNode));
    }

    @Override
    public T composeSingle(final String jsonText) {
        return composeSingle(isBlank(jsonText) ? null :jsonNodeMapper.compose(jsonText));
    }

    @Override
    public Collection<T> composeCollection(final JsonNode jsonNode) {
        throw new UnsupportedOperationException(format("%s: %s", getClass().getName(), "composeCollection"));
    }

    @Override
    public Collection<T> composeCollection(final String jsonText) {
        return composeCollection(isBlank(jsonText) ? null : jsonNodeMapper.compose(jsonText));
    }
}
