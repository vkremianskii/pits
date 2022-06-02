package com.github.vkremianskii.pits.core.log;

import org.slf4j.MDC;
import reactor.core.publisher.Signal;

import java.util.Set;
import java.util.function.Consumer;

public class LogUtils {

    private static final Set<String> CONTEXT_KEYS_WHITELIST = Set.of(
        "requestId"
    );

    private LogUtils() {
    }

    public static Consumer<Signal<?>> withMDCOnTerminate(Runnable block) {
        return signal -> {
            if (!signal.isOnComplete() && !signal.isOnError()) {
                return;
            }
            final var ctx = signal.getContextView();
            final var mdcCopy = MDC.getCopyOfContextMap();
            try {
                CONTEXT_KEYS_WHITELIST.forEach(key -> {
                    if (ctx.hasKey(key)) {
                        MDC.put(key, ctx.get(key).toString());
                    }
                });
                block.run();
            } finally {
                if (mdcCopy == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(mdcCopy);
                }
            }
        };
    }
}
