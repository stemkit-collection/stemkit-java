package com.bystr.sk.i18n.exception;

public class FallbackOverrideMessageDepotException extends MessageDepotException {
    private static final long serialVersionUID = 0;

    public FallbackOverrideMessageDepotException(final String code, final String fallback, final String existingFallback) {
        super("Replacing existing fallback", code, fallback, existingFallback);
    }
}
