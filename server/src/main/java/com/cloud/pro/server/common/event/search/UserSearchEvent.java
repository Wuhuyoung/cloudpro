package com.cloud.pro.server.common.event.search;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * 用户搜索事件
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UserSearchEvent extends ApplicationEvent {
    private static final long serialVersionUID = 3798302814735054745L;

    private String keyword;

    private Long userId;

    public UserSearchEvent(Object source, String keyword, Long userId) {
        super(source);
        this.keyword = keyword;
        this.userId = userId;
    }
}
