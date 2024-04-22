package com.cloud.pro.server.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.pro.server.modules.entity.UserSearchHistory;
import com.cloud.pro.server.modules.service.UserSearchHistoryService;
import com.cloud.pro.server.modules.mapper.UserSearchHistoryMapper;
import org.springframework.stereotype.Service;

/**
* @author 86183
* @description 针对表【cloud_pro_user_search_history(用户搜索历史表)】的数据库操作Service实现
* @createDate 2024-04-21 23:01:07
*/
@Service
public class UserSearchHistoryServiceImpl extends ServiceImpl<UserSearchHistoryMapper, UserSearchHistory>
    implements UserSearchHistoryService {

}




