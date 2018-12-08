package com.bystr.sk.http;

import org.springframework.http.HttpStatus;

/**
 * A base interface of the HTTP status reporting facility.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
public interface StatusSource {
    /**
     * Provides an HTTP status as a Spring's {@link HttpStatus}.
     *
     * @return [{@link HttpStatus}]
     *     HTTP status
    */
    HttpStatus getHttpStatus();
}
