package com.bystr.sk.i18n;

import static com.bystr.sk.util.ObjectUtils.tap;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Creates a message bundle based on the Spring's
 * {@link ResourceBundleMessageSource} implementation that reads the
 * corresponding resource bundle property file once on creation.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
public class StationaryMessageBundleFactory implements MessageBundleFactory {
    @Override
    public MessageBundle makeMessageBundle(final String bundleName) {
        return new CustomMessageBundle(bundleName);
    }

    private static class CustomMessageBundle implements MessageBundle {
        private final ResourceBundleMessageSource _resourceBundleMessageSource;

        CustomMessageBundle(final String bundleName) {
            _resourceBundleMessageSource = tap(new ResourceBundleMessageSource(), messageSource -> {
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
