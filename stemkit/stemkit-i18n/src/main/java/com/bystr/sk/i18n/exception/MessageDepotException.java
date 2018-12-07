package com.bystr.sk.i18n.exception;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;

import java.util.stream.Stream;

/**
 * A base exception class to report all message depot definition issues.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
public abstract class MessageDepotException extends RuntimeException {
    private static final long serialVersionUID = 0;
    private final String _message;

    /**
     * Constructs an exception instance with a message that consists of colon
     * separated exception type label and all constructor parameters converted
     * to strings.
     * <p>
     * @param values [{@link Object}...]
     *     values to add to the exception message
    */
    public MessageDepotException(final Object... values) {
        _message = concat(Stream.of(typeLabel()), Stream.of(values))
            .map(Object::toString)
            .collect(joining(": "));
    }

    /**
     * Returns a message that consists of colon separated exception type
     * label and all constructor parameters converted to strings.
     * <p>
     * @return [{@link String}]
     *     an exception message
    */
    @Override
    public String getMessage() {
        return _message;
    }

    private final String typeLabel() {
        return getClass().getSimpleName();
    }
}
