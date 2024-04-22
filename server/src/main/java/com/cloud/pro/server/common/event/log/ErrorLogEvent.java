package com.cloud.pro.server.common.event.log;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * 错误日志事件
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ErrorLogEvent extends ApplicationEvent {
    private static final long serialVersionUID = -542287519277426799L;

    /**
     * 错误日志
     */
    private String errorMsg;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    public ErrorLogEvent(Object source, String errorMsg, Long userId) {
        super(source);
        this.errorMsg = errorMsg;
        this.userId = userId;
    }
}
