package com.cloud.pro.stream.core;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component(value = "defaultStreamProducer")
public class DefaultStreamProducer extends AbstractStreamProducer {
    @Override
    protected void preSend(Message<Object> message) {

    }

    @Override
    protected void afterSend(Message<Object> message, boolean result) {

    }
}
