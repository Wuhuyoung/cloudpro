package com.cloud.pro.server.modules.vo;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询文件夹树节点 响应实体
 */
@Data
public class FolderTreeNodeVO implements Serializable {
    private static final long serialVersionUID = -5004414401401584094L;

    /**
     * 文件夹名称
     */
    private String label;

    /**
     * 文件ID
     */
    private Long id;

    /**
     * 文件夹ID
     */
    private Long parentId;

    /**
     * 子节点集合
     */
    private List<FolderTreeNodeVO> children;

    public void print() {
        String jsonString = JSON.toJSONString(this);
        System.out.println(jsonString);
    }
}
