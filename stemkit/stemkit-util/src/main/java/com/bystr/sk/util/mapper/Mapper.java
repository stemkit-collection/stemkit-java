package com.bystr.sk.util.mapper;

import com.fasterxml.jackson.databind.JsonNode;

public interface Mapper<T> {
    String serialize(final T object);

    T compose(final String jsonText);
    T compose(final JsonNode jsonNode);
}
