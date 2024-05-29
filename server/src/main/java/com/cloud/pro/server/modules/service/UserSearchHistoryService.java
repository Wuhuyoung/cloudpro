package com.cloud.pro.server.modules.service;

import com.cloud.pro.server.modules.context.user.QueryUserSearchHistoryContext;
import com.cloud.pro.server.modules.entity.UserSearchHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.pro.server.modules.vo.UserSearchHistoryVO;

import java.util.List;

/**
* @author han
* @description 针对表【cloud_pro_user_search_history(用户搜索历史表)】的数据库操作Service
* @createDate 2024-04-21 23:01:07
*/
public interface UserSearchHistoryService extends IService<UserSearchHistory> {

    /**
     * 查询用户最新的搜索历史记录，默认10条
     * @param context
     * @return
     */
    List<UserSearchHistoryVO> getUserSearchHistories(QueryUserSearchHistoryContext context);
}
