package com.cloud.pro.server.modules.converter;

import com.cloud.pro.server.modules.context.file.DeleteFileContext;
import com.cloud.pro.server.modules.context.file.SecUploadFileContext;
import com.cloud.pro.server.modules.context.file.UpdateFilenameContext;
import com.cloud.pro.server.modules.context.user.CreateFolderContext;
import com.cloud.pro.server.modules.po.file.CreateFolderPO;
import com.cloud.pro.server.modules.po.file.DeleteFilePO;
import com.cloud.pro.server.modules.po.file.SecUploadFilePO;
import com.cloud.pro.server.modules.po.file.UpdateFilenamePO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 文件实体转化工具类
 */
@Mapper(componentModel = "spring")
public interface FileConverter {

    @Mapping(target = "parentId", expression = "java(com.cloud.pro.core.utils.IdUtil.decrypt(createFolderPO.getParentId()))")
    @Mapping(target = "userId", expression = "java(com.cloud.pro.server.common.utils.UserIdUtil.get())")
    CreateFolderContext createFolderPO2Context(CreateFolderPO createFolderPO);

    @Mapping(target = "fileId", expression = "java(com.cloud.pro.core.utils.IdUtil.decrypt(updateFilenamePO.getFileId()))")
    @Mapping(target = "userId", expression = "java(com.cloud.pro.server.common.utils.UserIdUtil.get())")
    UpdateFilenameContext updateFilenamePO2Context(UpdateFilenamePO updateFilenamePO);

    @Mapping(target = "userId", expression = "java(com.cloud.pro.server.common.utils.UserIdUtil.get())")
    DeleteFileContext deleteFilePO2Context(DeleteFilePO deleteFilePO);

    @Mapping(target = "parentId", expression = "java(com.cloud.pro.core.utils.IdUtil.decrypt(secUploadFilePO.getParentId()))")
    @Mapping(target = "userId", expression = "java(com.cloud.pro.server.common.utils.UserIdUtil.get())")
    SecUploadFileContext secUploadFilePO2Context(SecUploadFilePO secUploadFilePO);
}
