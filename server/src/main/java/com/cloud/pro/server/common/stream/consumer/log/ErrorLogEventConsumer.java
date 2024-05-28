package com.cloud.pro.server.common.stream.consumer.log;

import com.cloud.pro.core.utils.IdUtil;
import com.cloud.pro.server.common.stream.channel.CloudProChannels;
import com.cloud.pro.server.common.stream.event.log.ErrorLogEvent;
import com.cloud.pro.server.modules.entity.ErrorLog;
import com.cloud.pro.server.modules.service.ErrorLogService;
import com.cloud.pro.stream.core.AbstractConsumer;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 系统错误日志监听器
 */
@Component
public class ErrorLogEventConsumer extends AbstractConsumer {
    @Resource
    private ErrorLogService errorLogService;

    /**
     * 监听系统错误日志事件，并保存到数据库
     * @param message
     */
    @StreamListener(CloudProChannels.ERROR_LOG_INPUT)
    public void saveErrorLog(Message<ErrorLogEvent> message) {
        if (isEmptyMessage(message)) {
            return;
        }
        printLog(message);
        ErrorLogEvent event = message.getPayload();

        ErrorLog errorLog = new ErrorLog();
        errorLog.setId(IdUtil.get());
        errorLog.setLogContent(event.getErrorMsg());
        errorLog.setLogStatus(0);
        errorLog.setCreateUser(event.getUserId());
        errorLog.setUpdateUser(event.getUserId());
        errorLogService.save(errorLog);
    }
}
