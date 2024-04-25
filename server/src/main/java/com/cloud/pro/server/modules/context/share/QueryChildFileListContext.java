package com.cloud.pro.server.modules.context.share;

import com.cloud.pro.server.modules.entity.Share;
import lombok.Data;

import java.io.Serializable;

/**
 * 获取下一级文件列表 上下文实体
 */
@Data
public class QueryChildFileListContext implements Serializable {
    private static final long serialVersionUID = 1261272705191997442L;

    /**
     * 分享的ID
     */
    private Long shareId;

    /**
     * 父文件夹ID
     */
    private Long parentId;

    /**
     * 分享对应的实体信息
     */
    private Share record;
}
