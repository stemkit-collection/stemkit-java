package com.bystr.sk.util.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Helps in mapping objects from and to YAML.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
public class YAMLMapper<T> extends AbstractMapper<T> {
    public YAMLMapper(final Class<T> targetClass) {
        super(targetClass, makeObjectMapper());
    }

    public YAMLMapper(final TypeReference<T> typeReference) {
        super(typeReference, makeObjectMapper());
    }

    private static final ObjectMapper makeObjectMapper() {
        return new ObjectMapper(new YAMLFactory());
    }
}
