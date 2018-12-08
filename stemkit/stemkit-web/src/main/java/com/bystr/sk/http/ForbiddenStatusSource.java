package com.bystr.sk.http;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import org.springframework.http.HttpStatus;

public interface ForbiddenStatusSource extends StatusSource {
    @Override
    default HttpStatus getHttpStatus() {
        return FORBIDDEN;
    }
}
