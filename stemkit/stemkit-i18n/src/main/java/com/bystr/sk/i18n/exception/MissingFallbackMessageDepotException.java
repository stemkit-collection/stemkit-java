package com.bystr.sk.i18n.exception;

public class MissingFallbackMessageDepotException extends MessageDepotException {
    private static final long serialVersionUID = 0;

    public MissingFallbackMessageDepotException(final String code) {
        super("Fallback not registered", code);
    }
}
