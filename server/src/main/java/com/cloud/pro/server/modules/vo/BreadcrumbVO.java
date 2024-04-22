package com.cloud.pro.server.modules.vo;

import com.cloud.pro.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

/**
 * 查询面包屑列表 响应实体
 */
@Data
public class BreadcrumbVO implements Serializable {
    private static final long serialVersionUID = -8784424195426797320L;

    /**
     * 文件ID
     */
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    /**
     * 父文件夹ID
     */
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    /**
     * 文件夹名称
     */
    private String name;
}
