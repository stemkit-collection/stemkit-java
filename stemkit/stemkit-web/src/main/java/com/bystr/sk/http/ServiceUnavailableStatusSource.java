package com.bystr.sk.http;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

import org.springframework.http.HttpStatus;

public interface ServiceUnavailableStatusSource extends StatusSource {
    @Override
    default HttpStatus getHttpStatus() {
        return SERVICE_UNAVAILABLE;
    }
}
