package com.bystr.sk.i18n.exception;

public class RealmMismatchMessageDepotException extends MessageDepotException {
    private static final long serialVersionUID = 0;

    public RealmMismatchMessageDepotException(final String realm, final String name) {
        super("Not under realm", realm, name);
    }
}
