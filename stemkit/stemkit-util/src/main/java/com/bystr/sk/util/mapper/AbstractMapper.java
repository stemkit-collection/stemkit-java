package com.bystr.sk.util.mapper;

import static com.bystr.sk.util.Holder.holdFrom;
import static com.bystr.sk.util.ObjectUtils.map;
import static java.lang.String.join;

import com.bystr.sk.util.ExceptionalSupplier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Helps in mapping objects from and to different serialization formats.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
public abstract class AbstractMapper<T> implements Mapper<T> {
    private final ObjectWriter _writer;
    private final ObjectReader _reader;
    private final String _typeName;

    protected AbstractMapper(final Class<T> targetClass, final ObjectMapper objectMapper) {
        _writer = objectMapper.writerFor(targetClass);
        _reader = objectMapper.readerFor(targetClass);

        _typeName = targetClass.getName();
    }

    protected AbstractMapper(final TypeReference<T> typeReference, final ObjectMapper objectMapper) {
        _writer = objectMapper.writerFor(typeReference);
        _reader = objectMapper.readerFor(typeReference);

        _typeName = typeReference.getType().getTypeName();
    }

    @Override
    public String serialize(final T object) {
        return map(object, () -> invoke("serialize", () -> _writer.writeValueAsString(object)));
    }

    @Override
    public T compose(final JsonNode jsonNode) {
        return map(jsonNode, () -> invoke("compose", () -> _reader.readValue(jsonNode)));
    }

    @Override
    public T compose(final String jsonText) {
        return map(jsonText, () -> invoke("compose", () -> _reader.readValue(jsonText)));
    }

    private <R> R invoke(final String label, final ExceptionalSupplier<R> supplier) {
        return holdFrom(() -> supplier.get())
            .whenException().thenRaise(exception -> new RuntimeException(makeMessage(label, exception), exception))
            .get();
    }

    private String makeMessage(final String label, final Exception exception) {
        return join(": ", getClass().getSimpleName(), label, _typeName, exception.getMessage());
    }
}
