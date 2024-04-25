package com.cloud.pro.server.modules.service;

import com.cloud.pro.server.enums.DelFlagEnum;
import com.cloud.pro.server.modules.context.file.*;
import com.cloud.pro.server.modules.context.user.CreateFolderContext;
import com.cloud.pro.server.modules.entity.UserFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.pro.server.modules.vo.BreadcrumbVO;
import com.cloud.pro.server.modules.vo.FileChunkUploadVO;
import com.cloud.pro.server.modules.vo.FileSearchResultVO;
import com.cloud.pro.server.modules.vo.FolderTreeNodeVO;
import com.cloud.pro.server.modules.vo.UploadedChunksVO;
import com.cloud.pro.server.modules.vo.UserFileVO;

import java.util.List;

/**
* @author han
* @description 针对表【cloud_pro_user_file(用户文件信息表)】的数据库操作Service
* @createDate 2024-02-26 23:00:21
*/
public interface UserFileService extends IService<UserFile> {
    /**
     * 创建文件夹信息
     * @param createFolderContext
     * @return 文件id
     */
    Long createFolder(CreateFolderContext createFolderContext);

    /**
     * 查询用户的根文件夹信息
     * @param userId
     * @return
     */
    UserFile getUserRootFile(Long userId);

    /**
     * 查询用户文件列表
     * @param queryFileListContext
     * @return
     */
    List<UserFileVO> getFileList(QueryFileListContext queryFileListContext);

    /**
     * 文件重命名
     * @param context
     */
    void updateFilename(UpdateFilenameContext context);

    /**
     * 批量删除文件
     * @param deleteFileContext
     */
    void deleteFile(DeleteFileContext deleteFileContext);

    /**
     * 文件秒传
     * @param context
     * @return
     */
    boolean secUpload(SecUploadFileContext context);

    /**
     * 单文件上传
     * @param context
     */
    void upload(FileUploadContext context);

    /**
     * 分片上传
     * @param context
     * @return
     */
    FileChunkUploadVO chunkUpload(FileChunkUploadContext context);

    /**
     * 查询已上传的文件分片列表
     * @param context
     * @return
     */
    UploadedChunksVO getUploadedChunks(QueryUploadedChunksContext context);

    /**
     * 文件分片合并
     * @param context
     */
    void mergeFile(FileChunkMergeContext context);

    /**
     * 文件下载
     * @param context
     */
    void download(FileDownloadContext context);

    /**
     * 文件下载，不校验用户是否是文件所有者
     * @param context
     */
    void downloadWithoutCheckUser(FileDownloadContext context);

    /**
     * 文件预览
     * @param context
     */
    void preview(FilePreviewContext context);

    /**
     * 查询文件夹树
     * @param context
     * @return
     */
    List<FolderTreeNodeVO> getFolderTree(QueryFolderTreeContext context);

    /**
     * 文件转移
     * @param context
     */
    void transfer(TransferFileContext context);

    /**
     * 文件复制
     * @param context
     */
    void copy(CopyFileContext context);

    /**
     * 文件搜索
     * @param context
     * @return
     */
    List<FileSearchResultVO> search(FileSearchContext context);

    /**
     * 查询面包屑列表
     * @param context
     * @return
     */
    List<BreadcrumbVO> getBreadcrumbs(QueryBreadcrumbsContext context);

    /**
     * 递归查询所有的子文件信息
     * @param records
     * @param delFlagEnum
     * @return
     */
    List<UserFile> findAllFileRecords(List<UserFile> records, DelFlagEnum delFlagEnum);

    /**
     * 递归查询所有的子文件信息
     * @param fileIdList
     * @return
     */
    List<UserFile> findAllFileRecordsByFileIdList(List<Long> fileIdList, DelFlagEnum delFlagEnum);
}
