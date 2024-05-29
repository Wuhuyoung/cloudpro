package com.cloud.pro.server.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.pro.server.modules.context.user.QueryUserSearchHistoryContext;
import com.cloud.pro.server.modules.entity.UserSearchHistory;
import com.cloud.pro.server.modules.service.UserSearchHistoryService;
import com.cloud.pro.server.modules.mapper.UserSearchHistoryMapper;
import com.cloud.pro.server.modules.vo.UserSearchHistoryVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author han
* @description 针对表【cloud_pro_user_search_history(用户搜索历史表)】的数据库操作Service实现
* @createDate 2024-04-21 23:01:07
*/
@Service
public class UserSearchHistoryServiceImpl extends ServiceImpl<UserSearchHistoryMapper, UserSearchHistory>
    implements UserSearchHistoryService {

    @Resource
    private UserSearchHistoryMapper mapper;

    /**
     * 查询用户最新的搜索历史记录，默认10条
     * @param context
     * @return
     */
    @Override
    public List<UserSearchHistoryVO> getUserSearchHistories(QueryUserSearchHistoryContext context) {
        return mapper.selectUserSearchHistories(context);
    }
}




