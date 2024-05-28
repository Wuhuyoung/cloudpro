package com.cloud.pro.server.common.stream.event;

import lombok.Data;

import java.io.Serializable;

/**
 * 测试事件
 */
@Data
public class TestEvent implements Serializable {
    private static final long serialVersionUID = -3498422007215220984L;

    private String name;
}
