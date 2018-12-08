/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.sk.i18n;

import java.io.PrintStream;
import java.util.function.Function;

/**
 * Defines message depot operations.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
public interface MessageDepot {
    /**
     * Sets up message depot to use only fallback (default) messages.
     * Messages defined in the resource bundle property files will be
     * ignored.
    */
    void useFallbacksOnly();

    /**
     * Sets up message depot to use only message codes. Messages defined in
     * the resource bundle property files and fallback (default) messages
     * will be ignored.
    */
    void useCodesOnly();

    /**
     * Sets up message depot to use messages from the resource bundle property
     * files if available, defaulting to fallback messages otherwise.
     * <p>
     * This is a default behavior for message depots.
    */
    void useResources();

    /**
     * Specifies whether to add message code prefixes to resulting output
     * messages.
     *
     * @param state [boolean]
     *     {@code true} &mdash; add message code prefixes to output messages;
     *     {@code false} (default) &mdash; do not add code prefixes
     *     </dl>
    */
    void useCodePrefix(final boolean state);

    /**
     * Returns a character string to be used for building resource bundle
     * file path. Normally implementing classes would figure the value
     * automatically based on the location of the implementing class in
     * package hierarchy.
     * <p>
     * @return [{@link String}
     *     a character string to be used for building resource
     *     bundle file path.
    */
    String bundleName();

    /**
     * Returns a character string to be used as a namespace for
     * this message bundle. Normally implementing classes would
     * figure the value automatically based on the location of
     * the implementing class in package hierarchy.
     * <p>
     * @return [{@link String}]
     *     a character string to be used as a namespace for
     *     this message bundle.
    */
    String namespace();

    /**
     * Outputs the content of the message depot in resource bundle property
     * file format using message codes as keys and the corresponding default
     * messages as values.
     *
     * @param director [{@link Function}<{@link String}, {@link PrintStream}>
     *     a lambda expression accepting a bundle name as a {@link String}
     *     parameter and returning a {@link PrintStream} to output the data
     *     into.
    */
    public void dump(final Function<String, PrintStream> director);
}
