/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.sk.i18n;

import static com.bystr.sk.util.ObjectUtils.tap;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Creates a message bundle based on the Spring's
 * {@link ReloadableResourceBundleMessageSource} implementation that picks up
 * the changes to the corresponding resource bundle property file.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
public class ReloadableMessageBundleFactory implements MessageBundleFactory {
    @Override
    public MessageBundle makeMessageBundle(final String bundleName) {
        return new CustomMessageBundle(bundleName);
    }

    private static class CustomMessageBundle implements MessageBundle {
        private final ReloadableResourceBundleMessageSource _resourceBundleMessageSource;

        CustomMessageBundle(final String bundleName) {
            _resourceBundleMessageSource = tap(new ReloadableResourceBundleMessageSource(), messageSource -> {
                messageSource.setUseCodeAsDefaultMessage(true);
                messageSource.setBasename(bundleName);
            });
        }

        @Override
        public MessageSource messageSource() {
            return _resourceBundleMessageSource;
        }

        @Override
        public void setParent(final MessageBundle parent) {
            _resourceBundleMessageSource.setParentMessageSource(parent.messageSource());
        }
    }
}
