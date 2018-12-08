/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

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
