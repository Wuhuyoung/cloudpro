package com.cloud.pro.server.modules.po.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 文件分片上传 请求参数对象
 */
@Data
public class FileChunkUploadPO implements Serializable {
    private static final long serialVersionUID = 1472443598470555331L;

    @ApiModelProperty(value = "文件名称", required = true)
    @NotBlank(message = "文件名称不能为空")
    private String filename;

    @ApiModelProperty(value = "文件的唯一标识", required = true)
    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;

    @ApiModelProperty(value = "总共的分片数", required = true)
    @NotNull(message = "总共的分片数不能为空")
    private Integer totalChunks;

    @ApiModelProperty(value = "当前分片的下标", required = true)
    @NotNull(message = "当前分片的下标不能为空")
    private Integer chunkNumber;

    @ApiModelProperty(value = "当前分片的大小，从1开始", required = true)
    @NotNull(message = "当前分片的大小不能为空")
    private Long currentChunkSize;

    @ApiModelProperty(value = "文件总大小", required = true)
    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;

    @ApiModelProperty(value = "分片文件实体", required = true)
    @NotNull(message = "分片文件实体不能为空")
    private MultipartFile file;
}
