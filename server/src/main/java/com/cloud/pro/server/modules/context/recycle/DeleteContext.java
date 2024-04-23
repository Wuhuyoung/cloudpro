package com.cloud.pro.server.modules.context.recycle;

import com.cloud.pro.server.modules.entity.UserFile;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文件彻底删除 上下文实体
 */
@Data
public class DeleteContext implements Serializable {
    private static final long serialVersionUID = 5780739005735617425L;

    /**
     * 要还原的文件ID集合
     */
    private List<Long> fileIdList;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 要被删除的文件记录列表
     */
    private List<UserFile> records;

    /**
     * 所有要被删除的文件记录列表
     */
    private List<UserFile> allRecords;
}
