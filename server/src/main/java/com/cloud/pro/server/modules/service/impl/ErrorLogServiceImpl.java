package com.cloud.pro.server.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.pro.server.modules.entity.ErrorLog;
import com.cloud.pro.server.modules.service.ErrorLogService;
import com.cloud.pro.server.modules.mapper.ErrorLogMapper;
import org.springframework.stereotype.Service;

/**
* @author han
* @description 针对表【cloud_pro_error_log(错误日志表)】的数据库操作Service实现
* @createDate 2024-04-17 21:12:49
*/
@Service
public class ErrorLogServiceImpl extends ServiceImpl<ErrorLogMapper, ErrorLog>
    implements ErrorLogService {

}




