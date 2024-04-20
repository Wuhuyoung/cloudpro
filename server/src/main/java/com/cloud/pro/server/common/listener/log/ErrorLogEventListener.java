package com.cloud.pro.server.common.listener.log;

import com.cloud.pro.core.utils.IdUtil;
import com.cloud.pro.server.common.event.ErrorLogEvent;
import com.cloud.pro.server.modules.entity.ErrorLog;
import com.cloud.pro.server.modules.service.ErrorLogService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 系统错误日志监听器
 */
@Component
public class ErrorLogEventListener {
    @Resource
    private ErrorLogService errorLogService;

    /**
     * 监听系统错误日志事件，并保存到数据库
     * @param event
     */
    @EventListener(ErrorLogEvent.class)
    public void saveErrorLog(ErrorLogEvent event) {
        ErrorLog errorLog = new ErrorLog();
        errorLog.setId(IdUtil.get());
        errorLog.setLogContent(event.getErrorMsg());
        errorLog.setLogStatus(0);
        errorLog.setCreateUser(event.getUserId());
        errorLog.setUpdateUser(event.getUserId());
        errorLogService.save(errorLog);
    }
}
