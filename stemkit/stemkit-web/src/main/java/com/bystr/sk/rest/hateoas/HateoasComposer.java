package com.bystr.sk.rest.hateoas;

import java.util.Collection;

import com.fasterxml.jackson.databind.JsonNode;

public interface HateoasComposer<T> {
    T composeSingle(final JsonNode jsonNode);
    T composeSingle(final String jsonText);

    Collection<T> composeCollection(final JsonNode jsonNode);
    Collection<T> composeCollection(final String jsonText);
}
