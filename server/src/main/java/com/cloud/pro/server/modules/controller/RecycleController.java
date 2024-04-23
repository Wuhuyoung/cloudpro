package com.cloud.pro.server.modules.controller;

import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.core.response.Result;
import com.cloud.pro.core.utils.IdUtil;
import com.cloud.pro.server.common.utils.UserIdUtil;
import com.cloud.pro.server.modules.context.recycle.DeleteContext;
import com.cloud.pro.server.modules.context.recycle.QueryRecycleFileListContext;
import com.cloud.pro.server.modules.context.recycle.RestoreContext;
import com.cloud.pro.server.modules.po.recycle.DeletePO;
import com.cloud.pro.server.modules.po.recycle.RestorePO;
import com.cloud.pro.server.modules.service.RecycleService;
import com.cloud.pro.server.modules.vo.UserFileVO;
import com.google.common.base.Splitter;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@Api(tags = "回收站模块")
@Validated
public class RecycleController {
    @Resource
    private RecycleService recycleService;

    /**
     * 查询回收站文件列表
     * @return
     */
    @GetMapping("/recycles")
    public Result<List<UserFileVO>> recycles() {
        QueryRecycleFileListContext context = new QueryRecycleFileListContext();
        context.setUserId(UserIdUtil.get());
        List<UserFileVO> result = recycleService.recycles(context);
        return Result.data(result);
    }

    /**
     * 删除的文件批量还原
     * @param restorePO
     * @return
     */
    @PutMapping("/recycle/restore")
    public Result<?> restore(@RequestBody @Validated RestorePO restorePO) {
        RestoreContext context = new RestoreContext();
        context.setUserId(UserIdUtil.get());

        String fileIds = restorePO.getFileIds();
        List<Long> fileIdList = Splitter.on(CommonConstants.COMMON_SEPARATOR).splitToList(fileIds)
                .stream().map(IdUtil::decrypt).collect(Collectors.toList());
        context.setFileIdList(fileIdList);

        recycleService.restore(context);
        return Result.success();
    }

    /**
     * 文件彻底删除
     * @param deletePO
     * @return
     */
    @DeleteMapping("/recycle")
    public Result<?> delete(@RequestBody @Validated DeletePO deletePO) {
        DeleteContext context = new DeleteContext();
        context.setUserId(UserIdUtil.get());

        String fileIds = deletePO.getFileIds();
        List<Long> fileIdList = Splitter.on(CommonConstants.COMMON_SEPARATOR).splitToList(fileIds)
                .stream().map(IdUtil::decrypt).collect(Collectors.toList());
        context.setFileIdList(fileIdList);

        recycleService.delete(context);
        return Result.success();
    }
}
