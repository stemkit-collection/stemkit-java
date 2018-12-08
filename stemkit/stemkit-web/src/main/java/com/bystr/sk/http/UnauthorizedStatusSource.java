package com.bystr.sk.http;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.http.HttpStatus;

public interface UnauthorizedStatusSource extends StatusSource {
    @Override
    default HttpStatus getHttpStatus() {
        return UNAUTHORIZED;
    }
}
