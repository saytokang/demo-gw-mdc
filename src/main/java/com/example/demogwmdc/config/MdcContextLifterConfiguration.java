package com.example.demogwmdc.config;

import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;
import reactor.util.context.Context;

@Component
public class MdcContextLifterConfiguration {
    public static final String MDC_CONTEXT_REACTOR_KEY = MdcContextLifterConfiguration.class.getName();

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void contextOperatorHook() {
        Hooks.onEachOperator(MDC_CONTEXT_REACTOR_KEY, Operators.lift((scannable, subscriber) -> new MdcContextLifter(subscriber)));
    }

    @PreDestroy
    public void cleanupHook() {
        Hooks.resetOnEachOperator(MDC_CONTEXT_REACTOR_KEY);
    }

    /**
     * Helper that copies the state of Reactor [Context] to MDC on the #onNext function.
     */
    @RequiredArgsConstructor
    public static class MdcContextLifter<T> implements CoreSubscriber<T> {

        private final CoreSubscriber<T> coreSubscriber;

        @Override
        public void onSubscribe(Subscription subscription) {
            coreSubscriber.onSubscribe(subscription);
        }

        @Override
        public void onNext(T t) {
            copyToMdc(coreSubscriber.currentContext());
            coreSubscriber.onNext(t);
        }

        @Override
        public void onError(Throwable throwable) {
            coreSubscriber.onError(throwable);
        }

        @Override
        public void onComplete() {
            coreSubscriber.onComplete();
        }

        @Override
        public Context currentContext() {
            return coreSubscriber.currentContext();
        }

        /**
         * Extension function for the Reactor [Context]. Copies the current context to the MDC, if context is empty clears the MDC.
         * State of the MDC after calling this method should be same as Reactor [Context] state.
         * One thread-local access only.
         */
        void copyToMdc(Context context) {
            if (context != null && !context.isEmpty()) {
                Map<String, String> map = context.stream()
                        .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
                MDC.setContextMap(map);
            } else {
                MDC.clear();
            }
        }
    }
}
