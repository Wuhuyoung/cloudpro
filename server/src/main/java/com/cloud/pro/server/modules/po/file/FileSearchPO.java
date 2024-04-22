package com.cloud.pro.server.modules.po.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件搜索 请求参数对象
 */
@Data
public class FileSearchPO implements Serializable {
    private static final long serialVersionUID = 997621289951689835L;

    @ApiModelProperty(value = "搜索的关键字", required = true)
    @NotBlank(message = "搜索的关键字不能为空")
    private String keyword;

    @ApiModelProperty(value = "文件类型，多个文件类型使用公用分隔符拼接")
    private String fileTypes;
}
