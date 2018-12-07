package com.bystr.sk.i18n.depot.i18n;

import com.bystr.sk.i18n.AbstractMessageDepot;
import com.bystr.sk.i18n.MessageBundleFactory;

public class TopLevelMessageDepot extends AbstractMessageDepot {
    private static final String M1 = "m1";
    private static final String M2 = "m2";

    public TopLevelMessageDepot(MessageBundleFactory messageBundleFactory) {
        super(messageBundleFactory);

        registerFallback(M1, "Message 1");
        registerFallback(M2, "Message 2");
    }

    public String m1() {
        return getMessage(M1);
    }

    public String m2() {
        return getMessage(M2);
    }
}
