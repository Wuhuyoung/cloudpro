package com.cloud.pro.server.modules.context.recycle;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文件还原 上下文实体
 */
@Data
public class RestoreContext implements Serializable {
    private static final long serialVersionUID = 4153579942287934547L;

    /**
     * 要还原的文件ID集合
     */
    private List<Long> fileIdList;

    /**
     * 用户ID
     */
    private Long userId;
}
