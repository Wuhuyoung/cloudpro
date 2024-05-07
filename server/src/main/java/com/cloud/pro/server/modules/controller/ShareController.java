package com.cloud.pro.server.modules.controller;

import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.core.response.Result;
import com.cloud.pro.core.utils.IdUtil;
import com.cloud.pro.server.common.annotation.LoginIgnore;
import com.cloud.pro.server.common.annotation.NeedShareCode;
import com.cloud.pro.server.common.utils.ShareIdUtil;
import com.cloud.pro.server.common.utils.UserIdUtil;
import com.cloud.pro.server.modules.context.share.CancelShareContext;
import com.cloud.pro.server.modules.context.share.CheckShareCodeContext;
import com.cloud.pro.server.modules.context.share.CreateShareUrlContext;
import com.cloud.pro.server.modules.context.share.QueryChildFileListContext;
import com.cloud.pro.server.modules.context.share.QueryShareDetailContext;
import com.cloud.pro.server.modules.context.share.QueryShareListContext;
import com.cloud.pro.server.modules.context.share.QueryShareSimpleDetailContext;
import com.cloud.pro.server.modules.context.share.ShareFileDownloadContext;
import com.cloud.pro.server.modules.context.share.ShareSaveContext;
import com.cloud.pro.server.modules.converter.ShareConverter;
import com.cloud.pro.server.modules.po.share.CancelSharePO;
import com.cloud.pro.server.modules.po.share.CheckShareCodePO;
import com.cloud.pro.server.modules.po.share.CreateShareUrlPO;
import com.cloud.pro.server.modules.po.share.ShareSavePO;
import com.cloud.pro.server.modules.service.ShareService;
import com.cloud.pro.server.modules.vo.ShareDetailVO;
import com.cloud.pro.server.modules.vo.ShareSimpleDetailVO;
import com.cloud.pro.server.modules.vo.ShareUrlListVO;
import com.cloud.pro.server.modules.vo.ShareUrlVO;
import com.cloud.pro.server.modules.vo.UserFileVO;
import com.google.common.base.Splitter;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@Api(tags = "分享模块")
@Validated
public class ShareController {
    @Resource
    private ShareService shareService;

    @Resource
    private ShareConverter shareConverter;

    /**
     * 创建分享链接
     * @param createShareUrlPO
     * @return
     */
    @PostMapping("/share")
    public Result<ShareUrlVO> create(@RequestBody @Validated CreateShareUrlPO createShareUrlPO) {
        CreateShareUrlContext context = shareConverter.createShareUrlPO2Context(createShareUrlPO);
        String shareFileIds = createShareUrlPO.getShareFileIds();
        List<Long> shareFileIdList = Splitter.on(CommonConstants.COMMON_SEPARATOR).splitToList(shareFileIds)
                .stream().map(IdUtil::decrypt).collect(Collectors.toList());
        context.setShareFileIdList(shareFileIdList);

        ShareUrlVO vo = shareService.create(context);
        return Result.data(vo);
    }

    /**
     * 查询分享链接列表
     * @return
     */
    @GetMapping("shares")
    public Result<List<ShareUrlListVO>> getShares() {
        QueryShareListContext context = new QueryShareListContext();
        context.setUserId(UserIdUtil.get());
        List<ShareUrlListVO> result = shareService.getShares(context);
        return Result.data(result);
    }

    /**
     * 取消分享
     * @param cancelSharePO
     * @return
     */
    @DeleteMapping("/share")
    public Result<?> cancelShare(@Validated @RequestBody CancelSharePO cancelSharePO) {
        CancelShareContext context = new CancelShareContext();
        context.setUserId(UserIdUtil.get());

        String shareIds = cancelSharePO.getShareIds();
        List<Long> shareIdList = Splitter.on(CommonConstants.COMMON_SEPARATOR).splitToList(shareIds)
                .stream().map(IdUtil::decrypt).collect(Collectors.toList());
        context.setShareIdList(shareIdList);

        shareService.cancelShare(context);
        return Result.success();
    }

    /**
     * 校验分享码
     * @param checkShareCodePO
     * @return
     */
    @LoginIgnore
    @PostMapping("/share/code/check")
    public Result<String> checkShareCode(@Validated @RequestBody CheckShareCodePO checkShareCodePO) {
        CheckShareCodeContext context = new CheckShareCodeContext();
        context.setShareId(IdUtil.decrypt(checkShareCodePO.getShareId()));
        context.setShareCode(checkShareCodePO.getShareCode());

        String token = shareService.checkShareCode(context);
        return Result.data(token);
    }

    /**
     * 查询分享详情
     * @return
     */
    @LoginIgnore
    @NeedShareCode
    @GetMapping("/share")
    public Result<ShareDetailVO> detail() {
        QueryShareDetailContext context = new QueryShareDetailContext();
        context.setShareId(ShareIdUtil.get());
        ShareDetailVO vo = shareService.detail(context);
        return Result.data(vo);
    }

    /**
     * 查询分享的简单详情
     * @param shareId
     * @return
     */
    @LoginIgnore
    @GetMapping("/share/simple")
    public Result<ShareSimpleDetailVO> simpleDetail(@NotBlank(message = "分享的ID不能为空") @RequestParam(value = "shareId") String shareId) {
        QueryShareSimpleDetailContext context = new QueryShareSimpleDetailContext();
        context.setShareId(IdUtil.decrypt(shareId));
        ShareSimpleDetailVO vo = shareService.simpleDetail(context);
        return Result.data(vo);
    }

    /**
     * 获取下一级文件列表
     * @param parentId
     * @return
     */
    @LoginIgnore
    @NeedShareCode
    @GetMapping("/share/file/list")
    public Result<List<UserFileVO>> fileList(@NotBlank(message = "文件的父ID不能为空") @RequestParam(value = "parentId") String parentId) {
        QueryChildFileListContext context = new QueryChildFileListContext();
        context.setShareId(ShareIdUtil.get());
        context.setParentId(IdUtil.decrypt(parentId));
        List<UserFileVO> result = shareService.fileList(context);
        return Result.data(result);
    }

    /**
     * 保存文件到我的文件夹
     * @param shareSavePO
     * @return
     */
    @NeedShareCode
    @PostMapping("/share/save")
    public Result<?> saveFiles(@Validated @RequestBody ShareSavePO shareSavePO) {
        ShareSaveContext context = new ShareSaveContext();
        String fileIds = shareSavePO.getFileIds();
        List<Long> fileIdList = Splitter.on(CommonConstants.COMMON_SEPARATOR).splitToList(fileIds)
                .stream().map(IdUtil::decrypt).collect(Collectors.toList());
        context.setFileIdList(fileIdList);
//        context.setTargetParentId(IdUtil.decrypt(shareSavePO.getTargetParentId()));
        context.setTargetParentId(Long.valueOf(shareSavePO.getTargetParentId()));
        context.setUserId(UserIdUtil.get());
        context.setShareId(ShareIdUtil.get());

        shareService.saveFiles(context);
        return Result.success();
    }

    /**
     * 分享文件下载
     * @param fileId
     * @param response
     */
    @GetMapping("/share/file/download")
    @NeedShareCode
    public void download(@NotBlank(message = "文件ID不能为空") @RequestParam(value = "fileId") String fileId,
                         HttpServletResponse response) {
        ShareFileDownloadContext context = new ShareFileDownloadContext();
        context.setFileId(IdUtil.decrypt(fileId));
        context.setUserId(UserIdUtil.get());
        context.setShareId(ShareIdUtil.get());
        context.setResponse(response);
        shareService.download(context);
    }
}
