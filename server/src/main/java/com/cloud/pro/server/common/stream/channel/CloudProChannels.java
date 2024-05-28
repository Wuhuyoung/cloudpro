package com.cloud.pro.server.common.stream.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 事件通道定义类
 */
public interface CloudProChannels {

    String TEST_INPUT = "testInput";
    String TEST_OUTPUT = "testOutput";

    String ERROR_LOG_INPUT = "errorLogInput";
    String ERROR_LOG_OUTPUT = "errorLogOutput";

    String DELETE_FILE_INPUT = "deleteFileInput";
    String DELETE_FILE_OUTPUT = "deleteFileOutput";

    String FILE_RESTORE_INPUT = "fileRestoreInput";
    String FILE_RESTORE_OUTPUT = "fileRestoreOutput";

    String PHYSICAL_DELETE_FILE_INPUT = "physicalDeleteFileInput";
    String PHYSICAL_DELETE_FILE_OUTPUT = "physicalDeleteFileOutput";

    String USER_SEARCH_INPUT = "userSearchInput";
    String USER_SEARCH_OUTPUT = "userSearchOutput";

    /**
     * 测试输入通道
     * @return
     */
    @Input(TEST_INPUT)
    SubscribableChannel input();

    /**
     * 测试输出通道
     * @return
     */
    @Output(TEST_OUTPUT)
    MessageChannel output();

    @Input(ERROR_LOG_INPUT)
    SubscribableChannel errorLogInput();

    @Output(ERROR_LOG_OUTPUT)
    MessageChannel errorLogOutput();

    @Input(DELETE_FILE_INPUT)
    SubscribableChannel deleteFileInput();

    @Output(DELETE_FILE_OUTPUT)
    MessageChannel deleteFileOutput();

    @Input(FILE_RESTORE_INPUT)
    SubscribableChannel fileRestoreInput();

    @Output(FILE_RESTORE_OUTPUT)
    MessageChannel fileRestoreOutput();

    @Input(PHYSICAL_DELETE_FILE_INPUT)
    SubscribableChannel physicalDeleteFileInput();

    @Output(PHYSICAL_DELETE_FILE_OUTPUT)
    MessageChannel physicalDeleteFileOutput();

    @Input(USER_SEARCH_INPUT)
    SubscribableChannel userSearchInput();

    @Output(USER_SEARCH_OUTPUT)
    MessageChannel userSearchOutput();
}
