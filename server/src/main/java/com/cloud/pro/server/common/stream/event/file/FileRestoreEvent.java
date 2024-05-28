package com.cloud.pro.server.common.stream.event.file;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.List;

/**
 * 文件还原事件
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class FileRestoreEvent implements Serializable {
    private static final long serialVersionUID = -4009861422177629571L;
    /**
     * 被成功还原的文件ID集合
     */
    private List<Long> fileIdList;

    public FileRestoreEvent(List<Long> fileIdList) {
        this.fileIdList = fileIdList;
    }
}
