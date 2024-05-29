package com.cloud.pro.server.common.stream.event.search;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * 用户搜索事件
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class UserSearchEvent implements Serializable {
    private static final long serialVersionUID = 3798302814735054745L;

    private String keyword;

    private Long userId;

    public UserSearchEvent(String keyword, Long userId) {
        this.keyword = keyword;
        this.userId = userId;
    }
}
