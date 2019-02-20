/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm;

import static com.bystr.stm.util.ObjectUtils.map;
import static com.bystr.stm.util.ObjectUtils.tap;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.bystr.stm.http.StatusSource;
import com.bystr.stm.i18n.LocalizableException;
import com.bystr.stm.i18n.MessageDepot;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class OutboundException extends LocalizableException implements StatusSource {
    private static final long serialVersionUID = 0;

    private final transient Consumer<Registry> _registry;
    private final transient Info _info;

    public OutboundException(final MessageDepot messageDepot, final Consumer<Registry> registry) {
        super(messageDepot);

        _registry = requireNonNull(registry);
        _info = makeInfo();
    }

    public final Info makeInfo() {
        return tap(new Collector(), _registry::accept);
    }

    public String getDetails() {
        return _info.details();
    }

    public String getReason() {
        return _info.reason();
    }

    public String getRemediation() {
        return _info.remediation();
    }

    public int getStatusCode() {
        return getHttpStatus().value();
    }

    @Override
    public String getMessage() {
        final String details = _info.details();
        final String reason = _info.reason();

        if (details == null) {
            return reason;
        }

        return format("%s (%s)", reason, details);
    }

    public ObjectNode toJsonObjectNode() {
        return map(JsonNodeFactory.instance.objectNode(), node -> {
            tap(makeInfo(), info -> {
                node.put("code", getStatusCode());
                node.put("reason", info.reason());
                node.put("remediation", info.remediation());
            });

            return node;
        });
    }

    public interface Info {
        String reason();
        String remediation();
        String details();
    }

    protected interface Registry {
        Registry reason(final String message);
        Registry details(final String message);
        Registry remediation(final String message);
    }

    private static class Collector implements Registry, Info {
        private String _reason;
        private String _details;
        private String _remediation;

        @Override
        public String reason() {
            return _reason;
        }

        @Override
        public String remediation() {
            return _remediation;
        }

        @Override
        public String details() {
            return _details;
        }

        @Override
        public Registry reason(final String text) {
            _reason = text;
            return this;
        }

        @Override
        public Registry details(final String text) {
            _details = text;
            return this;
        }

        @Override
        public Registry remediation(final String text) {
            _remediation = text;
            return this;
        }
    }
}
