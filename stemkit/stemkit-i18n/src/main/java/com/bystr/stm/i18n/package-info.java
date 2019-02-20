/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

/**
 * Extended support for localization based on Java resource bundles and
 * message formatting capabilities, as well on Spring facilities for locating,
 * loading, and processing resource bundles.
 * <p>
 * It also makes it possible to switch to ICU (International Components for
 * Unicode, see <a href="http://site.icu-project.org">site.icu-project.org</a>
 * for details) if the current Java implementation is not sufficient (e.g.,
 * support for advanced pluralization is needed).
 * <p>
 * This package provides capabilities for implementing message depots organizing
 * them in nested structures. Unlike other similar facilities, this one does not
 * stop at just defining message codes. It also defines methods for corresponding
 * messages, fixing up exact parameters those messages require.
 * <p>
 * By keeping default messages in the code, it is possible to work even without
 * resource bundles if none are available. The facility also allows to generate
 * initial content for resource bundles with default messages, making it easier
 * to start translating to another language.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
package com.bystr.stm.i18n;
