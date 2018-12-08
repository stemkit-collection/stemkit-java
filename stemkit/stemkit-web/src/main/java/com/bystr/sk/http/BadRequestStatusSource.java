package com.bystr.sk.http;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.http.HttpStatus;

public interface BadRequestStatusSource extends StatusSource {
    @Override
    default HttpStatus getHttpStatus() {
        return BAD_REQUEST;
    }
}
