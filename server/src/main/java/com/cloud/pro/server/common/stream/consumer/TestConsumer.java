package com.cloud.pro.server.common.stream.consumer;

import com.cloud.pro.server.common.stream.channel.CloudProChannels;
import com.cloud.pro.server.common.stream.event.TestEvent;
import com.cloud.pro.stream.core.AbstractConsumer;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * 测试消息消费者
 */
@Component
public class TestConsumer extends AbstractConsumer {

    /**
     * 消费消息测试
     * @param message
     */
    @StreamListener(CloudProChannels.TEST_INPUT)
    public void consumeTestMessage(Message<TestEvent> message) {
        printLog(message);
    }
}
