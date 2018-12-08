package com.bystr.sk.http;

import static org.springframework.http.HttpStatus.EXPECTATION_FAILED;

import org.springframework.http.HttpStatus;

public interface ExpectationFailedStatusSource extends StatusSource {
    @Override
    default HttpStatus getHttpStatus() {
        return EXPECTATION_FAILED;
    }
}
