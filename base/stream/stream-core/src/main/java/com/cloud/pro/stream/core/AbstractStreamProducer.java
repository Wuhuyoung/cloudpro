package com.cloud.pro.stream.core;

import com.cloud.pro.core.exception.FrameworkException;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * 消息发送者顶级父类
 */
public abstract class AbstractStreamProducer implements IStreamProducer {
    @Resource
    private Map<String, MessageChannel> channelMap;

    @Override
    public boolean sendMessage(String channelName, Object deploy) {
        return sendMessage(channelName, deploy, Maps.newHashMap());
    }

    @Override
    public boolean sendMessage(String channelName, Object deploy, Map<String, Object> headers) {
        // 1.参数校验
        if (StringUtils.isBlank(channelName) || Objects.isNull(deploy)) {
            throw new FrameworkException("the channelName or deploy cannot be empty!");
        }
        if (MapUtils.isEmpty(channelMap)) {
            throw new FrameworkException("the channelMap cannot be empty!");
        }
        MessageChannel channel = channelMap.get(channelName);
        if (Objects.isNull(channel)) {
            throw new FrameworkException("the channel named " + channelName + " cannot be found!");
        }
        Message<Object> message = MessageBuilder.createMessage(deploy, new MessageHeaders(headers));
        // 2.发送前的钩子函数
        preSend(message);
        // 3.执行发送
        boolean result = channel.send(message);
        // 4.发送后的钩子函数
        afterSend(message, result);
        // 5.返回结果
        return result;
    }

    /**
     * 发送消息的前置钩子函数
     * @param message
     */
    protected abstract void preSend(Message<Object> message);

    /**
     * 发送消息的后置钩子函数
     * @param message
     * @param result
     */
    protected abstract void afterSend(Message<Object> message, boolean result);
}
