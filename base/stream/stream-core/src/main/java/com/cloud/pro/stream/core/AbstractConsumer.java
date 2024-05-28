package com.cloud.pro.stream.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;

import java.util.Objects;

/**
 * 消费者的共用父类
 */
@Slf4j
public abstract class AbstractConsumer {

    /**
     * 记录日志
     * @param message
     */
    protected void printLog(Message message) {
        log.info("{} start consume message, the message is {}", this.getClass().getSimpleName(), message);
    }

    /**
     * 消息判空校验
     * @param message
     * @return
     */
    protected boolean isEmptyMessage(Message message) {
        if (Objects.isNull(message) || Objects.isNull(message.getPayload())) {
            return true;
        }
        return false;
    }
}
