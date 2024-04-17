package com.cloud.pro.server.modules.mapper;

import com.cloud.pro.server.modules.context.file.QueryFileListContext;
import com.cloud.pro.server.modules.entity.UserFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.pro.server.modules.vo.UserFileVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author han
* @description 针对表【cloud_pro_user_file(用户文件信息表)】的数据库操作Mapper
* @createDate 2024-02-26 23:00:21
* @Entity generator.domain.CloudProUserFile
*/
public interface UserFileMapper extends BaseMapper<UserFile> {

    List<UserFileVO> selectFileList(@Param("context") QueryFileListContext queryFileListContext);
}




