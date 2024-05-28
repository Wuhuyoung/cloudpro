package com.cloud.pro.server.common.stream.consumer.search;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cloud.pro.core.utils.IdUtil;
import com.cloud.pro.server.common.stream.channel.CloudProChannels;
import com.cloud.pro.server.common.stream.event.search.UserSearchEvent;
import com.cloud.pro.server.modules.entity.UserSearchHistory;
import com.cloud.pro.server.modules.service.UserSearchHistoryService;
import com.cloud.pro.stream.core.AbstractConsumer;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 用户搜索事件监听器
 */
@Component
public class UserSearchEventConsumer extends AbstractConsumer {
    @Resource
    private UserSearchHistoryService userSearchHistoryService;

    /**
     * 监听用户搜索事件，保存到用户的搜索历史记录中
     * @param
     */
    @StreamListener(CloudProChannels.USER_SEARCH_INPUT)
    public void saveSearchHistory(Message<UserSearchEvent> message) {
        if (isEmptyMessage(message)) {
            return;
        }
        printLog(message);
        UserSearchEvent event = message.getPayload();

        UserSearchHistory history = new UserSearchHistory();
        history.setId(IdUtil.get());
        history.setUserId(event.getUserId());
        history.setSearchContent(event.getKeyword());
        try {
            userSearchHistoryService.save(history);
        } catch (DuplicateKeyException e) {
            // user_id和search_content加了唯一索引，插入可能会报异常
            // 说明已经有该搜索记录，直接更新搜索时间即可，不用再插入新纪录
            LambdaUpdateWrapper<UserSearchHistory> luw = new LambdaUpdateWrapper<>();
            luw.eq(UserSearchHistory::getUserId, event.getUserId());
            luw.eq(UserSearchHistory::getSearchContent, event.getKeyword());
            luw.set(UserSearchHistory::getUpdateTime, LocalDateTime.now());
            userSearchHistoryService.update(luw);
        }
    }
}
