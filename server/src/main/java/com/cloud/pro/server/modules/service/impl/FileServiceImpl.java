package com.cloud.pro.server.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.pro.server.modules.entity.File;
import com.cloud.pro.server.modules.service.FileService;
import com.cloud.pro.server.modules.mapper.FileMapper;
import org.springframework.stereotype.Service;

/**
* @author han
* @description 针对表【cloud_pro_file(物理文件信息表)】的数据库操作Service实现
* @createDate 2024-04-16 19:44:30
*/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File>
    implements FileService {

}




