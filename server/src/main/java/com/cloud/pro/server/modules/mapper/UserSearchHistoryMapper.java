package com.cloud.pro.server.modules.mapper;

import com.cloud.pro.server.modules.context.user.QueryUserSearchHistoryContext;
import com.cloud.pro.server.modules.entity.UserSearchHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.pro.server.modules.vo.UserSearchHistoryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author han
* @description 针对表【cloud_pro_user_search_history(用户搜索历史表)】的数据库操作Mapper
* @createDate 2024-04-21 23:01:07
* @Entity generator.domain.CloudProUserSearchHistory
*/
public interface UserSearchHistoryMapper extends BaseMapper<UserSearchHistory> {

    /**
     * 查询用户的最近十条搜索历史记录
     * @param context
     * @return
     */
    List<UserSearchHistoryVO> selectUserSearchHistories(@Param("context") QueryUserSearchHistoryContext context);
}




