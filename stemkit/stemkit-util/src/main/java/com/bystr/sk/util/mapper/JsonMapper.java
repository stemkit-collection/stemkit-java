package com.bystr.sk.util.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Helps in mapping objects from and to JSON.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
public class JsonMapper<T> extends AbstractMapper<T> {
    public JsonMapper(final Class<T> targetClass) {
        super(targetClass, makeObjectMapper());
    }

    public JsonMapper(final TypeReference<T> typeReference) {
        super(typeReference, makeObjectMapper());
    }

    private static final ObjectMapper makeObjectMapper() {
        return new ObjectMapper();
    }
}
