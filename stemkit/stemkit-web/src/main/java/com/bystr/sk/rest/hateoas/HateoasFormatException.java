package com.bystr.sk.rest.hateoas;

public class HateoasFormatException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public HateoasFormatException() {
        super("Wrong HATEOAS data");
    }
}
