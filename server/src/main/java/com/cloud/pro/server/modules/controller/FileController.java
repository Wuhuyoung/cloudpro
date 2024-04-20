package com.cloud.pro.server.modules.controller;

import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.core.response.Result;
import com.cloud.pro.core.utils.IdUtil;
import com.cloud.pro.server.common.utils.UserIdUtil;
import com.cloud.pro.server.constants.FileConstants;
import com.cloud.pro.server.enums.DelFlagEnum;
import com.cloud.pro.server.modules.context.file.DeleteFileContext;
import com.cloud.pro.server.modules.context.file.FileChunkMergeContext;
import com.cloud.pro.server.modules.context.file.FileChunkUploadContext;
import com.cloud.pro.server.modules.context.file.FileUploadContext;
import com.cloud.pro.server.modules.context.file.QueryFileListContext;
import com.cloud.pro.server.modules.context.file.QueryUploadedChunksContext;
import com.cloud.pro.server.modules.context.file.SecUploadFileContext;
import com.cloud.pro.server.modules.context.file.UpdateFilenameContext;
import com.cloud.pro.server.modules.context.user.CreateFolderContext;
import com.cloud.pro.server.modules.converter.FileConverter;
import com.cloud.pro.server.modules.po.file.CreateFolderPO;
import com.cloud.pro.server.modules.po.file.DeleteFilePO;
import com.cloud.pro.server.modules.po.file.FileChunkMergePO;
import com.cloud.pro.server.modules.po.file.FileChunkUploadPO;
import com.cloud.pro.server.modules.po.file.FileUploadPO;
import com.cloud.pro.server.modules.po.file.QueryUploadedChunksPO;
import com.cloud.pro.server.modules.po.file.SecUploadFilePO;
import com.cloud.pro.server.modules.po.file.UpdateFilenamePO;
import com.cloud.pro.server.modules.service.UserFileService;
import com.cloud.pro.server.modules.vo.FileChunkUploadVO;
import com.cloud.pro.server.modules.vo.UploadedChunksVO;
import com.cloud.pro.server.modules.vo.UserFileVO;
import com.google.common.base.Splitter;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@Api(tags = "文件模块")
@Validated
public class FileController {
    @Resource
    private UserFileService userFileService;

    @Resource
    private FileConverter fileConverter;

    /**
     * 查询文件列表
     * @param parentId
     * @param fileTypes
     * @return
     */
    @GetMapping("/files")
    public Result<List<UserFileVO>> list(@NotBlank(message = "父文件ID不能为空") @RequestParam(value = "parentId") String parentId,
                                   @RequestParam(value = "fileTypes", required = false, defaultValue = FileConstants.ALL_FILE_TYPE) String fileTypes) {
        Long realParentId = IdUtil.decrypt(parentId);
        List<Integer> fileTypeArray = null;
        if (!Objects.equals(fileTypes, FileConstants.ALL_FILE_TYPE)) {
            fileTypeArray = Splitter.on(CommonConstants.COMMON_SEPARATOR).splitToList(fileTypes).stream().map(Integer::valueOf).collect(Collectors.toList());
        }
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setParentId(realParentId);
        queryFileListContext.setUserId(UserIdUtil.get());
        queryFileListContext.setFileTypeArray(fileTypeArray);
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        List<UserFileVO> result = userFileService.getFileList(queryFileListContext);
        return Result.data(result);
    }

    /**
     * 创建文件夹
     * @param createFolderPO
     * @return
     */
    @PostMapping("/file/folder")
    public Result<String> createFolder(@RequestBody @Validated CreateFolderPO createFolderPO) {
        CreateFolderContext context = fileConverter.createFolderPO2Context(createFolderPO);
        Long fileId = userFileService.createFolder(context);
        return Result.data(IdUtil.encrypt(fileId));
    }

    /**
     * 文件重命名
     * @param updateFilenamePO
     * @return
     */
    @PutMapping("/file")
    public Result<?> updateFilename(@RequestBody @Validated UpdateFilenamePO updateFilenamePO) {
        UpdateFilenameContext context = fileConverter.updateFilenamePO2Context(updateFilenamePO);
        userFileService.updateFilename(context);
        return Result.success();
    }

    /**
     * 批量删除文件
     * @param deleteFilePO
     * @return
     */
    @DeleteMapping("/file")
    public Result<?> deleteFile(@RequestBody @Validated DeleteFilePO deleteFilePO) {
        DeleteFileContext deleteFileContext = fileConverter.deleteFilePO2Context(deleteFilePO);
        // 将文件ID分割成List，同时进行解密
        String fileIds = deleteFilePO.getFileIds();
        List<Long> fileIdList = Splitter.on(CommonConstants.COMMON_SEPARATOR).splitToList(fileIds)
                .stream().map(IdUtil::decrypt).collect(Collectors.toList());
        deleteFileContext.setFileIdList(fileIdList);

        userFileService.deleteFile(deleteFileContext);
        return Result.success();
    }

    /**
     * 文件秒传
     * @param secUploadFilePO
     * @return
     */
    @PostMapping("/file/sec-upload")
    public Result<?> secUpload(@RequestBody @Validated SecUploadFilePO secUploadFilePO) {
        SecUploadFileContext secUploadFileContext = fileConverter.secUploadFilePO2Context(secUploadFilePO);
        boolean success = userFileService.secUpload(secUploadFileContext);
        if (success) {
            return Result.success();
        }
        return Result.fail();
    }

    /**
     * 单文件上传
     * @param fileUploadPO
     * @return
     */
    @PostMapping("/file/upload")
    // 前端传来的Content-Type: multipart/form-data，而@RequestBody的参数只能设置为 Content-Type: application/json
    // 所以这里不能在参数前加上 @RequestBody
    public Result<?> upload(@Validated FileUploadPO fileUploadPO) {
        FileUploadContext fileUploadContext = fileConverter.fileUploadPO2Context(fileUploadPO);
        userFileService.upload(fileUploadContext);
        return Result.success();
    }

    /**
     * 文件分片上传
     * @param fileChunkUploadPO
     * @return
     */
    @PostMapping("/file/chunk-upload")
    public Result<FileChunkUploadVO> chunkUpload(@Validated FileChunkUploadPO fileChunkUploadPO) {
        FileChunkUploadContext context = fileConverter.fileChunkUploadPO2Context(fileChunkUploadPO);
        FileChunkUploadVO vo = userFileService.chunkUpload(context);
        return Result.data(vo);
    }

    /**
     * 查询已上传的文件分片列表
     * @param queryUploadedChunksPO
     * @return
     */
    @GetMapping("/file/chunk-upload")
    public Result<UploadedChunksVO> getUploadedChunks(@Validated QueryUploadedChunksPO queryUploadedChunksPO) {
        QueryUploadedChunksContext context = fileConverter.queryUploadedChunksPO2Context(queryUploadedChunksPO);
        UploadedChunksVO vo = userFileService.getUploadedChunks(context);
        return Result.data(vo);
    }

    /**
     * 文件分片合并
     * @param fileChunkMergePO
     * @return
     */
    @PostMapping("/file/merge")
    public Result<?> mergeFile(@Validated @RequestBody FileChunkMergePO fileChunkMergePO) {
        FileChunkMergeContext context = fileConverter.fileChunkMergePO2Context(fileChunkMergePO);
        userFileService.mergeFile(context);
        return Result.success();
    }
}
