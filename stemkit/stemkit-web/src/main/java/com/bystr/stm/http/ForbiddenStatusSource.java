/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm.http;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import org.springframework.http.HttpStatus;

public interface ForbiddenStatusSource extends StatusSource {
    @Override
    default HttpStatus getHttpStatus() {
        return FORBIDDEN;
    }
}
