/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

/**
 * Extension to Spring's HTTP support library, defines interfaces for reporting
 * HTTP status codes, normally for exception classes designed to be converted
 * to the corresponding HTTP responses.
 * <p>
 * It makes it possible by just providing implementation for interface method
 * {@link StatusSource#getHttpStatus()} to have particular HTTP status reported
 * by simply implementing a particular interface corresponding to that status.
 * <p>
 * For example,
 * <pre>
 * public class HttpStatusReporter implements ForbiddenStatusSource {
 *     ...
 *     {@code @Override}
 *     public HttpStatus getHttpStatus() {
 *         return HTTP_STATUS;
 *     }
 * }
 * </pre>
 * By just replacing {@link ForbiddenStatusSource} with {@link LockedStatusSource} the
 * above class would return status {@link org.springframework.http.HttpStatus#LOCKED}
 * without a need to modify anything else in the implementation.
 * <p>
 * With this approach in place, it makes it possible to see all classes in IDE that would
 * provide a certain HTTP status code.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
package com.bystr.sk.http;
