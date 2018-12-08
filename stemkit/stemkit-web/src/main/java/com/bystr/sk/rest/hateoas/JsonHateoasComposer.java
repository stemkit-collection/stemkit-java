package com.bystr.sk.rest.hateoas;

import static com.bystr.sk.util.ObjectUtils.tap;
import static com.bystr.sk.util.StreamUtils.streamFrom;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonHateoasComposer<T> extends AbstractHateoasComposer<T> {
    public JsonHateoasComposer(final Class<T> targetClass) {
        super(targetClass);
    }

    @Override
    public Collection<T> composeCollection(final JsonNode node) {
        if (node == null) {
            return emptyList();
        }

        ensureThat(node.isObject());
        ensureThat(node.size() == 3);
        ensureThat(node.has("links"));
        ensureThat(node.has("content"));
        ensureThat(node.has("page"));

        final JsonNode content = node.get("content");
        ensureThat(content != null && content.isArray());

        return streamFrom(content)
            .map(this::composeSingle)
            .collect(toList());
    }

    @Override
    protected JsonNode prepareForSingle(final JsonNode jsonNode) {
        return tap(jsonNode, () -> {
            if (jsonNode.isObject()) {
                final ObjectNode objectNode = (ObjectNode) jsonNode;
                objectNode.remove("links");
            }
        });
    }

    private void ensureThat(final boolean condition) {
        if (condition == false) {
            throw new HateoasFormatException();
        }
    }
}
