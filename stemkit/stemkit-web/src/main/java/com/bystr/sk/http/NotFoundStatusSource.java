package com.bystr.sk.http;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.http.HttpStatus;

public interface NotFoundStatusSource extends StatusSource {
    @Override
    default HttpStatus getHttpStatus() {
        return NOT_FOUND;
    }
}
