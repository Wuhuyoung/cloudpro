package com.cloud.pro.server.modules.service;

import com.cloud.pro.server.modules.context.recycle.DeleteContext;
import com.cloud.pro.server.modules.context.recycle.QueryRecycleFileListContext;
import com.cloud.pro.server.modules.context.recycle.RestoreContext;
import com.cloud.pro.server.modules.vo.UserFileVO;

import java.util.List;

/**
 * 回收站模块
 */
public interface RecycleService {
    /**
     * 查询回收站文件列表
     * @param context
     * @return
     */
    List<UserFileVO> recycles(QueryRecycleFileListContext context);

    /**
     * 文件还原
     * @param context
     */
    void restore(RestoreContext context);

    /**
     * 文件彻底删除
     * @param context
     */
    void delete(DeleteContext context);
}
