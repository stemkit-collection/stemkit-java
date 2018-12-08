package com.bystr.sk.http;

import static org.springframework.http.HttpStatus.CONFLICT;

import org.springframework.http.HttpStatus;

public interface ConflictStatusSource extends StatusSource {
    @Override
    default HttpStatus getHttpStatus() {
        return CONFLICT;
    }
}
