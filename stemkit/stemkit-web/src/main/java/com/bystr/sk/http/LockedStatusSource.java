package com.bystr.sk.http;

import static org.springframework.http.HttpStatus.LOCKED;

import org.springframework.http.HttpStatus;

public interface LockedStatusSource extends StatusSource {
    @Override
    default HttpStatus getHttpStatus() {
        return LOCKED;
    }
}
