package com.cloud.pro.server.modules.mapper;

import com.cloud.pro.server.modules.entity.Share;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author han
* @description 针对表【cloud_pro_share(用户分享表)】的数据库操作Mapper
* @createDate 2024-04-23 22:23:32
* @Entity generator.domain.CloudProShare
*/
public interface ShareMapper extends BaseMapper<Share> {

    /**
     * 滚动查询已存在的分享ID集合
     * @param startId
     * @param batchSize
     * @return
     */
    List<Long> rollingQueryShareId(@Param("startId") long startId, @Param("batchSize")  long batchSize);
}




