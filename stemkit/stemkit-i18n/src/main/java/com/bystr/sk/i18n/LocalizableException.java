/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.sk.i18n;

/**
 * The base exception class for all localizable exceptions.
 * <p>
 * The main purpose of this class is to accept message depot reference and to
 * make subclasses define their own {@link #getMessage()} method
 * implementation completely abandoning the one in the base class.
 * <p>
 * Such abandonment is needed because the base exception class implementation
 * makes up the message from its constructor parameters, and as we do not pass
 * any parameters in the {@link Exception}'s constructor such implementation
 * does not make much sense.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
public abstract class LocalizableException extends RuntimeException {
    private static final long serialVersionUID = 0;
    private final transient MessageDepot _messageDepot;

    /**
     * Constructs a localizable exception instance.
     * <p>
     * @param messageDepot [{@link MessageDepot}]
     *     a message depot to use.
     */
    public LocalizableException(final MessageDepot messageDepot) {
        _messageDepot = messageDepot;
    }

    /**
     * Returns a message describing this localizable exception. The message
     * should normally consist of the default text for the message so that
     * in the information is the log could be understood by developers.
     * <p>
     * @return [{@link String}]
     *     a message describing this localizable exception.
    */
    @Override
    public abstract String getMessage();

    /**
     * Returns a message depot passed in the constructor.
     * <p>
     * @return [{@link MessageDepot}]
     *     a message depot passed in the constructor.
     */
    protected MessageDepot messageDepot() {
        return _messageDepot;
    }
}
