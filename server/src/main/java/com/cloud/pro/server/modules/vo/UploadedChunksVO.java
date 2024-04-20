package com.cloud.pro.server.modules.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询用户已上传的文件分片列表 响应实体
 */
@Data
public class UploadedChunksVO implements Serializable {
    private static final long serialVersionUID = 5757491988899007264L;

    /**
     * 已上传的分片编号列表
     */
    private List<Integer> uploadedChunks;
}
