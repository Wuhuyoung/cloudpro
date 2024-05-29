package com.cloud.pro.server.common.stream.event.test;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * 测试事件
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class TestEvent implements Serializable {
    private static final long serialVersionUID = 4060618979312111050L;

    private String message;

    public TestEvent(String message) {
        this.message = message;
    }
}
