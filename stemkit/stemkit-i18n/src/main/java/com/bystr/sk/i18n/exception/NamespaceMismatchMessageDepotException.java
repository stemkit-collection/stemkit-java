package com.bystr.sk.i18n.exception;

public class NamespaceMismatchMessageDepotException extends MessageDepotException {
    private static final long serialVersionUID = 0;

    public NamespaceMismatchMessageDepotException(final String code, final String properNamespace) {
        super("Namespace mismatch", code, properNamespace);
    }
}
