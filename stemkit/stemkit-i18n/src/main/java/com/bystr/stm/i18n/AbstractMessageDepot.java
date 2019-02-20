/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.stm.i18n;

import static com.bystr.stm.util.ObjectUtils.map;
import static com.bystr.stm.util.ObjectUtils.tap;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.copyOfRange;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.hasText;

import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.bystr.stm.i18n.exception.FallbackOverrideMessageDepotException;
import com.bystr.stm.i18n.exception.MissingFallbackMessageDepotException;
import com.bystr.stm.i18n.exception.NamespaceMismatchMessageDepotException;
import com.bystr.stm.i18n.exception.RealmMismatchMessageDepotException;

/**
 * The main building block for message depots.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
public abstract class AbstractMessageDepot implements MessageDepot {
    private static final String REALM_PACKAGE_COMPONENT = "i18n";
    private static final String TOP_BUNDLE_NAME = "root";

    private final List<AbstractMessageDepot> _depots = new ArrayList<>();
    private final Map<String, String> _fallbacks = new TreeMap<>();

    private final List<String> _namespaceComponents;
    private final MessageBundle _messageBundle;

    private BiFunction<String, Object[], String> _maker = this::makeResourceMessage;
    private boolean _useCodePrefix = false;

    public AbstractMessageDepot(final MessageBundleFactory messageBundleFactory) {
        _namespaceComponents = namespaceComponentsFromClassName(this.getClass().getName());
        _messageBundle = messageBundleFactory.makeMessageBundle(bundleName());
    }

    @Override
    public void dump(final Function<String, PrintStream> director) {
        tap(director.apply(bundleName()), printer -> {
            tap(new TreeMap<>(_fallbacks), fallbacks -> {
                fallbacks.forEach((code, fallback) -> {
                    printer.format("%s = %s%n", code, fallback);
                });
            });

            printer.flush();
        });

        _depots.stream()
            .sorted(AbstractMessageDepot::compareNamespaces)
            .forEach(depot -> depot.dump(director));
    }

    @Override
    public final void useFallbacksOnly() {
        _depots.forEach(AbstractMessageDepot::useFallbacksOnly);
        _maker = this::makeFallbackMessage;
    }

    @Override
    public final void useCodesOnly() {
        _depots.forEach(AbstractMessageDepot::useCodesOnly);
        _maker = this::makeCodeMessage;
    }

    @Override
    public final void useResources() {
        _depots.forEach(AbstractMessageDepot::useResources);
        _maker = this::makeResourceMessage;
    }

    @Override
    public final void useCodePrefix(final boolean state) {
        _depots.forEach(depot -> depot.useCodePrefix(state));
        _useCodePrefix = state;
    }

    @Override
    public final String bundleName() {
        return map(namespaceComponentsJoined("-"), name ->
            Paths.get("messages", hasText(name) ? name : TOP_BUNDLE_NAME).toString()
        );
    }

    @Override
    public final String namespace() {
        return namespaceComponentsJoined(".");
    }

    private final String namespaceComponentsJoined(final String separator) {
        return _namespaceComponents.stream().collect(joining(separator));
    }

    protected final <T extends AbstractMessageDepot> T registerMessageDepot(final T depot) {
        depot.messageBundle().setParent(_messageBundle);
        _depots.add(depot);

        return depot;
    }

    protected final MessageBundle messageBundle() {
        return _messageBundle;
    }

    protected final void registerFallback(final String code, final String fallback) {
        tap(_fallbacks.put(ensureNamespace(requireNonNull(code)), requireNonNull(fallback)), value -> {
            throw new FallbackOverrideMessageDepotException(code, fallback, value);
        });
    }

    protected final String getMessage(final String code, final Object... objects) {
        return _maker.apply(requireNonNull(code), objects);
    }

    private final String makeCodeMessage(final String code, final Object[] objects) {
        return makeMessage(null, code, objects, null);
    }

    private final String makeFallbackMessage(final String code, final Object[] objects) {
        return makeMessage(null, codeFallback(code), objects, codePrefix(code));
    }

    private final String makeResourceMessage(final String code, final Object[] objects) {
        return makeMessage(code, codeFallback(code), objects, codePrefix(code));
    }

    private final String makeMessage(final String code, final String fallback, final Object[] objects, final String codePrefix) {
        return map(
            _messageBundle.messageSource().getMessage(code, objects, fallback, Locale.getDefault()),
            message -> (codePrefix == null ? message : format("[%s] %s", codePrefix, message))
        );
    }

    private final String codeFallback(final String code) {
        final String fallback = _fallbacks.get(code);

        if (fallback == null) {
            throw new MissingFallbackMessageDepotException(code);
        }

        return fallback;
    }

    private final String codePrefix(final String code) {
        return _useCodePrefix == false ? null : code;
    }

    private final String ensureNamespace(final String code) {
        if (Objects.equals(namespaceComponents(code), _namespaceComponents) == false) {
            throw new NamespaceMismatchMessageDepotException(code, namespace());
        }

        return code;
    }

    private static final List<String> namespaceComponents(final String path) {
        return map(path.split("[.]"), components -> asList(copyOfRange(components, 0, components.length - 1)));
    }

    private static final List<String> namespaceComponentsFromClassName(final String className) {
        final List<String> components = namespaceComponents(className);
        final long index = components.lastIndexOf(REALM_PACKAGE_COMPONENT);

        if (index < 0) {
            throw new RealmMismatchMessageDepotException(REALM_PACKAGE_COMPONENT, className);
        }

        return components.stream().skip(index + 1).collect(toList());
    }

    private static final int compareNamespaces(final MessageDepot first, final MessageDepot second) {
        return first.namespace().compareTo(second.namespace());
    }
}
