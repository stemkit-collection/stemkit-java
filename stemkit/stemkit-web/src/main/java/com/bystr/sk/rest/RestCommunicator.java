/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.sk.rest;

import static com.bystr.sk.util.ExceptionUtils.forceRuntimeWhenException;
import static com.bystr.sk.util.ExceptionUtils.ignoreWhenException;
import static com.bystr.sk.util.ExceptionUtils.runtimeExceptionUnlessAlready;
import static com.bystr.sk.util.Holder.holdFrom;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import java.net.URI;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import com.bystr.sk.util.Holder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestCommunicator<T> {
    private static final Logger logger = LoggerFactory.getLogger(RestCommunicator.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final UnaryOperator<String> _urlMaker;
    private final RestOperations _restOperations;
    private final HttpHeaders _headers = new HttpHeaders();

    private String _port;
    private Object _body;
    private Class<T> _classReference;
    private ParameterizedTypeReference<T> _typeReference;

    public RestCommunicator(final RestOperations restOperations, final UnaryOperator<String> urlMaker) {
        _restOperations = restOperations;
        _urlMaker = urlMaker;
    }

    public RestCommunicator<T> body(final Object body) {
        _body = body;
        return this;
    }

    public RestCommunicator<T> headers(final HttpHeaders headers) {
        _headers.putAll(headers);
        return this;
    }

    public RestCommunicator<T> header(final String headerName, final String headerValue) {
        _headers.add(headerName, headerValue);
        return this;
    }

    public RestCommunicator<T> as(final Class<T> classReference) {
        _classReference = classReference;
        return this;
    }

    public RestCommunicator<T> as(final ParameterizedTypeReference<T> typeReference) {
        _typeReference = typeReference;
        return this;
    }

    public RestCommunicator<T> system() {
        return port(8443);
    }

    public RestCommunicator<T> port(final int portNumber) {
        _port = Integer.toString(portNumber);
        return this;
    }

    public RestCommunicator<T> port(final String portSpec) {
        return port(Integer.parseInt(portSpec));
    }

    public ResponseEntity<T> get(final String resource) {
        return perform(GET, resource);
    }

    public ResponseEntity<T> post(final String resource) {
        return perform(POST, resource);
    }

    public ResponseEntity<T> put(final String resource) {
        return perform(PUT, resource);
    }

    public ResponseEntity<T> delete(final String resource) {
        return perform(DELETE, resource);
    }

    public ResponseEntity<T> performLeakingExceptions(final HttpMethod method, final String resource) {
        return makeRequest(method, resource, true);
    }

    public ResponseEntity<T> perform(final HttpMethod method, final String resource) {
        return makeRequest(method, resource, false);
    }

    private ResponseEntity<T> makeRequest(final HttpMethod method, final String resource, final boolean leakExceptions) {
        final URI uri = forceRuntimeWhenException(() -> new URI(_urlMaker.apply(_port) + resource));
        final HttpEntity<?> entity = new HttpEntity<>(_body, _headers);

        if (_classReference != null) {
            return ensureResponse(leakExceptions, () -> _restOperations.exchange(uri, method, entity, _classReference));
        }

        if (_typeReference != null) {
            return ensureResponse(leakExceptions, () -> _restOperations.exchange(uri, method, entity, _typeReference));
        }

        throw new IllegalStateException("Unknown response type for http request");
    }

    private static <T> ResponseEntity<T> ensureResponse(final boolean leakExceptions, final Supplier<ResponseEntity<T>> responseSupplier) {
        final Holder<ResponseEntity<T>> responseHolder = holdFrom(responseSupplier::get);

        if (leakExceptions == true) {
            return responseHolder
                .whenException().thenRaiseForcingRuntime()
                .get();
        }

        final String label = "httpRequest";

        return responseHolder
            .whenException(HttpStatusCodeException.class).thenSet(exception -> {
                logger.info("{}: HTTP status: {}: {}", label, exception.getStatusCode(), exception.getStatusText());

                ignoreWhenException(() -> {
                    final JsonNode node = mapper.readValue(exception.getResponseBodyAsString(), JsonNode.class);

                    final String reason = node.get("reason").asText();
                    final String remediation = node.get("remediation").asText();

                    logger.info("{}: reason: {}", label, reason);
                    logger.info("{}: remediation: {}", label, remediation);
                });

                return new ResponseEntity<>(null, exception.getResponseHeaders(), exception.getStatusCode());
            })
            .whenException(UnknownHttpStatusCodeException.class).thenRaise(exception -> {
                logger.info("{}: Unknown HTTP status code {}: {}", label, exception.getRawStatusCode(), exception.getStatusText());
                return exception;
            })
            .whenException().thenRaise(exception -> {
                logger.info("{}: Unexpected exception: {}: {}", label, exception.getClass().getName(), exception.getMessage());
                return runtimeExceptionUnlessAlready(exception);
            })
            .get();
    }
}
