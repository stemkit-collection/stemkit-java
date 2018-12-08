package com.bystr.sk.http;

import static org.springframework.http.HttpStatus.FOUND;

import org.springframework.http.HttpStatus;

public interface FoundStatusSource extends StatusSource {

    @Override
    default HttpStatus getHttpStatus() {
         return FOUND;
    }
}
