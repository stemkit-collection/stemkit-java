/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.sk.i18n;

import org.springframework.context.MessageSource;

/**
 * Defines message bundle operations.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
public interface MessageBundle {
    /**
     * Queries a Spring's message source corresponding to (normally created by)
     * this bundle.
     * <p>
     * @return [{@link MessageSource}]
     *     a message source corresponding to this bundle.
    */
    MessageSource messageSource();

    /**
     * Sets the specified message bundle as a parent of this one.
     * <p>
     * @param parent [{@link MessageBundle}]
     *     a message source to set as a parent of this bundle.
    */
    void setParent(final MessageBundle parent);
}
