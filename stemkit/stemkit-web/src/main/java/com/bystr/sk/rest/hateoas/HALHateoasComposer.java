package com.bystr.sk.rest.hateoas;

public class HALHateoasComposer<T> extends AbstractHateoasComposer<T> {
    public HALHateoasComposer(final Class<T> targetClass) {
        super(targetClass);
    }
}
