package com.cloud.pro.server.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.pro.bloom.filter.core.BloomFilter;
import com.cloud.pro.bloom.filter.core.BloomFilterManager;
import com.cloud.pro.core.constants.CommonConstants;
import com.cloud.pro.core.exception.BusinessException;
import com.cloud.pro.core.response.ResponseCode;
import com.cloud.pro.core.utils.IdUtil;
import com.cloud.pro.core.utils.JwtUtil;
import com.cloud.pro.core.utils.UUIDUtil;
import com.cloud.pro.server.common.cache.ManualCacheService;
import com.cloud.pro.server.common.config.ServerConfig;
import com.cloud.pro.server.common.stream.channel.CloudProChannels;
import com.cloud.pro.server.common.stream.event.log.ErrorLogEvent;
import com.cloud.pro.server.constants.FileConstants;
import com.cloud.pro.server.constants.ShareConstants;
import com.cloud.pro.server.enums.DelFlagEnum;
import com.cloud.pro.server.enums.ShareDayTypeEnum;
import com.cloud.pro.server.enums.ShareStatusEnum;
import com.cloud.pro.server.modules.context.file.CopyFileContext;
import com.cloud.pro.server.modules.context.file.FileDownloadContext;
import com.cloud.pro.server.modules.context.file.QueryFileListContext;
import com.cloud.pro.server.modules.context.share.CancelShareContext;
import com.cloud.pro.server.modules.context.share.CheckShareCodeContext;
import com.cloud.pro.server.modules.context.share.CreateShareUrlContext;
import com.cloud.pro.server.modules.context.share.QueryChildFileListContext;
import com.cloud.pro.server.modules.context.share.QueryShareDetailContext;
import com.cloud.pro.server.modules.context.share.QueryShareListContext;
import com.cloud.pro.server.modules.context.share.QueryShareSimpleDetailContext;
import com.cloud.pro.server.modules.context.share.SaveShareFilesContext;
import com.cloud.pro.server.modules.context.share.ShareFileDownloadContext;
import com.cloud.pro.server.modules.context.share.ShareSaveContext;
import com.cloud.pro.server.modules.converter.FileConverter;
import com.cloud.pro.server.modules.converter.ShareConverter;
import com.cloud.pro.server.modules.entity.Share;
import com.cloud.pro.server.modules.entity.ShareFile;
import com.cloud.pro.server.modules.entity.User;
import com.cloud.pro.server.modules.entity.UserFile;
import com.cloud.pro.server.modules.mapper.ShareMapper;
import com.cloud.pro.server.modules.service.ShareFileService;
import com.cloud.pro.server.modules.service.ShareService;
import com.cloud.pro.server.modules.service.UserFileService;
import com.cloud.pro.server.modules.service.UserService;
import com.cloud.pro.server.modules.vo.ShareDetailVO;
import com.cloud.pro.server.modules.vo.ShareSimpleDetailVO;
import com.cloud.pro.server.modules.vo.ShareUrlListVO;
import com.cloud.pro.server.modules.vo.ShareUrlVO;
import com.cloud.pro.server.modules.vo.ShareUserInfoVO;
import com.cloud.pro.server.modules.vo.UserFileVO;
import com.cloud.pro.stream.core.IStreamProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author han
* @description 针对表【cloud_pro_share(用户分享表)】的数据库操作Service实现
* @createDate 2024-04-23 22:23:32
*/
@Service
@Slf4j
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareService {

    @Resource
    private ServerConfig config;

    @Resource
    private ShareFileService shareFileService;

    @Resource
    private UserFileService userFileService;

    @Resource
    private UserService userService;

    @Resource
    private ShareConverter converter;

    @Resource
    private FileConverter fileConverter;

    @Resource(name = "shareManualCacheService")
    private ManualCacheService<Share> cacheService;

    @Resource
    private BloomFilterManager manager;

    private static final String BLOOM_FILTER_NAME = "SHARE_SIMPLE_DETAIL";

    @Resource(name = "defaultStreamProducer")
    private IStreamProducer producer;

    /**
     * 创建分享链接
     * @param context
     * @return
     */
    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public ShareUrlVO create(CreateShareUrlContext context) {
        // 1.拼装分享实体，保存到数据库
        saveShare(context);
        // 2.保存分享和对应文件的关联关系
        saveShareFile(context);
        // 3.拼装返回实体并返回
        ShareUrlVO vo = new ShareUrlVO();
        Share record = context.getRecord();
        vo.setShareId(record.getShareId());
        vo.setShareName(record.getShareName());
        vo.setShareUrl(record.getShareUrl());
        vo.setShareCode(record.getShareCode());
        vo.setShareStatus(record.getShareStatus());
        // 4.后置操作，将创建的shareId插入布隆过滤器中
        afterCreate(context);
        return vo;
    }

    /**
     * 查询用户的分享列表
     * @param context
     * @return
     */
    @Override
    public List<ShareUrlListVO> getShares(QueryShareListContext context) {
        LambdaQueryWrapper<Share> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Share::getCreateUser, context.getUserId());
        List<Share> shareList = this.list(lqw);
        List<ShareUrlListVO> result = shareList.stream()
                .map(share -> converter.share2ShareUrlListVO(share))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * 取消分享
     * @param context
     */
    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public void cancelShare(CancelShareContext context) {
        List<Long> shareIdList = context.getShareIdList();
        Long userId = context.getUserId();
        // 1.校验用户操作权限
        List<Share> shareList = this.listByIds(shareIdList);
        for (Share share : shareList) {
            if (!Objects.equals(userId, share.getCreateUser())) {
                throw new BusinessException("您没有取消该分享的操作权限");
            }
        }
        // 2.删除对应的分享记录
        if (!this.removeByIds(shareIdList)) {
            throw new BusinessException("取消分享失败");
        }
        // 3.删除对应的分享文件关联关系
        LambdaUpdateWrapper<ShareFile> luw = new LambdaUpdateWrapper<>();
        luw.in(ShareFile::getShareId, shareIdList);
        luw.eq(ShareFile::getCreateUser, userId);
        if (!shareFileService.remove(luw)) {
            throw new BusinessException("取消分享失败");
        }
    }

    /**
     * 校验分享码
     * @param context
     * @return
     */
    @Override
    public String checkShareCode(CheckShareCodeContext context) {
        Long shareId = context.getShareId();
        String shareCode = context.getShareCode();

        // 1.校验分享的状态是否正常
        Share record = checkShareStatus(shareId);
        // 2.校验分享码
        if (!Objects.equals(record.getShareCode(), shareCode)) {
            throw new BusinessException("分享码错误");
        }

        // 3.生成一个限时的分享token
        String token = JwtUtil.generateToken(UUIDUtil.getUUID(), ShareConstants.SHARE_ID, shareId, ShareConstants.ONE_HOUR_LONG);
        return token;
    }

    /**
     * 查询分享详情
     * @param context
     * @return
     */
    @Override
    public ShareDetailVO detail(QueryShareDetailContext context) {
        Long shareId = context.getShareId();
        // 1.校验分享的状态
        Share record = checkShareStatus(shareId);
        context.setRecord(record);
        // 2.查询分享信息
        ShareDetailVO vo = new ShareDetailVO();
        vo.setShareId(record.getShareId());
        vo.setShareName(record.getShareName());
        vo.setCreateTime(record.getCreateTime());
        vo.setShareDay(record.getShareDay());
        vo.setShareEndTime(record.getShareEndTime());
        context.setVo(vo);

        // 3.查询分享的文件列表
        assembleShareFilesInfo(context);
        // 4.查询分享者的信息
        assembleShareUserInfo(context);
        return context.getVo();
    }

    /**
     * 查询分享的简单详情
     * @param context
     * @return
     */
    @Override
    public ShareSimpleDetailVO simpleDetail(QueryShareSimpleDetailContext context) {
        Long shareId = context.getShareId();
        // 1.校验分享的状态
        Share record = checkShareStatus(shareId);
        context.setRecord(record);
        // 2.查询分享信息
        ShareSimpleDetailVO vo = new ShareSimpleDetailVO();
        vo.setShareId(shareId);
        vo.setShareName(record.getShareName());
        context.setVo(vo);

        // 3.查询分享者的信息
        assembleShareSimpleUserInfo(context);
        return context.getVo();
    }

    /**
     * 获取下一级文件列表
     * @param context
     * @return
     */
    @Override
    public List<UserFileVO> fileList(QueryChildFileListContext context) {
        Long shareId = context.getShareId();
        Long parentId = context.getParentId();

        // 1.校验分享的状态
        Share record = checkShareStatus(shareId);
        context.setRecord(record);
        // 2.校验父文件的ID是在分享的文件列表中（可能是分享的文件列表的子文件，所以需要递归查询分享文件的所有子文件，判断parentId是否在其中）
        // 3.查询下一级文件列表
        // 这里的疑问解答：为什么不直接查询parentId下一级的文件，而是需要将分享的所有子文件都查询出来？
        // 解答：一开始保存分享文件映射信息，就是保存的用户勾选的文件，可能其中有文件夹，但是该文件夹下面的文件并没有保存映射信息
        //     而这个获取下一级列表的接口是不需要登录的，所以我们不信任这个接口，需要验证传过来的parentId是否在分享的文件中
        //     由于只通过share_file我们无法获取所有分享的文件，所以需要通过递归查询所有分享文件（包含文件夹下的文件）
        //     所以已经查询出来了所有分享文件，直接通过userFileVOList获取其中parentId为用户给定的即可
        List<UserFileVO> userFileVOList = checkFileIdIsOnShareStatusAndGetAllShareUserFiles(shareId, Lists.newArrayList(parentId));
        List<UserFileVO> result = userFileVOList.stream().filter(userFileVO -> Objects.equals(userFileVO.getParentId(), parentId))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * 保存文件到我的文件夹
     * @param context
     */
    @Override
    public void saveFiles(ShareSaveContext context) {
        // 1.校验分享状态
        checkShareStatus(context.getShareId());
        // 2.校验文件ID是否在分享列表中
        checkFileIdIsOnShareStatus(context.getShareId(), context.getFileIdList());
        // 3.委托文件模块做文件拷贝
        CopyFileContext copyFileContext = new CopyFileContext();
        copyFileContext.setFileIdList(context.getFileIdList());
        copyFileContext.setTargetParentId(context.getTargetParentId());
        copyFileContext.setUserId(context.getUserId());
        userFileService.copy(copyFileContext);
    }

    /**
     * 分享文件下载
     * @param context
     */
    @Override
    public void download(ShareFileDownloadContext context) {
        // 1.校验分享状态
        checkShareStatus(context.getShareId());
        // 2.校验文件ID是否在分享列表中
        checkFileIdIsOnShareStatus(context.getShareId(), Lists.newArrayList(context.getFileId()));
        // 3.委托文件模块下载
        doDownload(context);
    }

    /**
     * 刷新受影响的对应的分享状态
     * @param fileIdList
     */
    @Override
    public void refreshShareStatus(List<Long> fileIdList) {
        // 1.查询所有受影响的分享的ID集合
        List<Long> shareIdList = getShareIdListByFileIdList(fileIdList);
        // 2.判断每一个分享对应的文件以及所有的父文件夹都是正常，这种情况，把该分享的状态变为正常
        // 3.如果有分享的文件或其上层文件夹被删除，则变更该分享的状态为 有文件被删除
        for (Long shareId : shareIdList) {
            refreshShareStatus(shareId);
        }
    }

    /**
     * 滚动查询已存在的分享ID
     * @param startId
     * @param batchSize
     * @return
     */
    @Override
    public List<Long> rollingQueryShareId(long startId, long batchSize) {
        return baseMapper.rollingQueryShareId(startId, batchSize);
    }

    @Override
    public boolean removeById(Serializable id) {
        return cacheService.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<?> list) {
        return cacheService.removeByIds((Collection<? extends Serializable>) list);
    }

    @Override
    public boolean updateById(Share entity) {
        return cacheService.updateById(entity.getShareId(), entity);
    }

    @Override
    public boolean updateBatchById(Collection<Share> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return true;
        }
        Map<Long, Share> entityMap = entityList.stream().collect(Collectors.toMap(Share::getShareId, e -> e));
        return cacheService.updateByIds(entityMap);
    }

    @Override
    public Share getById(Serializable id) {
        return cacheService.getById(id);
//        return baseMapper.selectById(id);
    }

    @Override
    public List<Share> listByIds(Collection<? extends Serializable> idList) {
        return cacheService.getByIds(idList);
//        return baseMapper.selectBatchIds(idList);
    }

    /**********************************private**********************************/

    /**
     * 创建分享链接后置操作
     * @param context
     */
    private void afterCreate(CreateShareUrlContext context) {
        // 将创建的shareId插入布隆过滤器中
        BloomFilter bloomFilter = manager.getFilter(BLOOM_FILTER_NAME);
        if (Objects.nonNull(bloomFilter)) {
            bloomFilter.put(context.getRecord().getShareId());
            log.info("create share, share id is {}, add share id to bloom filter", context.getRecord().getShareId());
        }
    }

    /**
     * 刷新一个分享的分享状态
     * @param shareId
     */
    private void refreshShareStatus(Long shareId) {
        // 1.查询对应的分享，判断有效
        Share record = this.getById(shareId);
        if (Objects.isNull(record)) {
            return;
        }
        // 疑问：为什么不直接将受影响的分享设置为有文件被删除，既然查询到了说明确实有文件被删除啊
        // 解答：我觉得这样区分开来处理也是可以的，对于删除文件事件，查询到受影响的分享修改状态为 有文件被删除
        //                                对于还原文件事件，查询到受影响的分享修改状态为 正常
        //      但是这里这样处理可能是为了两个方法进行代码复用，同时刷新分享状态可能在其他方法中也会被调用

        // 2.判断该分享对应的文件以及所有的父文件夹都是正常，这种情况，把该分享的状态变为正常
        // 3.如果分享的文件或其上层文件夹被删除，则变更该分享的状态为 有文件被删除
        ShareStatusEnum shareStatus = ShareStatusEnum.NORMAL;
        if (!checkShareFileAvailable(shareId)) {
            shareStatus = ShareStatusEnum.FILE_DELETED;
        }
        // 分享状态未改变
        if (Objects.equals(record.getShareStatus(), shareStatus.getCode())) {
            return;
        }
        // 分享状态改变
        record.setShareStatus(shareStatus.getCode());
        if (!this.updateById(record)) {
            ErrorLogEvent event = new ErrorLogEvent(String.format("分享状态更新失败，请手动更新状态，分享ID为：%d，分享状态改为：%d", shareId, shareStatus.getCode()),
                    CommonConstants.ZERO_LONG);
            producer.sendMessage(CloudProChannels.ERROR_LOG_OUTPUT, event);
        }
    }

    /**
     * 检查该分享所有的文件以及所有的父文件均为正常状态
     * @param shareId
     * @return
     */
    private boolean checkShareFileAvailable(Long shareId) {
        List<Long> shareFileIdList = getShareFileIdList(shareId);
        for (Long fileId : shareFileIdList) {
            if (!checkUpFileAvailable(fileId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查该文件以及所有的父文件夹信息均为正常状态
     * @param fileId
     * @return
     */
    private boolean checkUpFileAvailable(Long fileId) {
        UserFile record = userFileService.getById(fileId);
        if (Objects.isNull(record)) {
            // 文件记录已经不存在
            return false;
        }
        if (Objects.equals(record.getDelFlag(), DelFlagEnum.YES.getCode())) {
            // 该文件(夹)被删除
            return false;
        }
        if (Objects.equals(FileConstants.TOP_PARENT_ID, record.getParentId())) {
            // 已经是顶级文件夹
            return true;
        }
        return checkUpFileAvailable(record.getParentId());
    }

    /**
     * 通过文件ID查找对应的分享ID集合
     * @param fileIdList
     * @return
     */
    private List<Long> getShareIdListByFileIdList(List<Long> fileIdList) {
        LambdaQueryWrapper<ShareFile> lqw = new LambdaQueryWrapper<>();
        lqw.select(ShareFile::getShareId);
        lqw.in(ShareFile::getFileId, fileIdList);
        // 如果只需要查询一个字段，可以使用listObjs，其中第二个mapper函数是对于查询出来的第一个字段的处理，因为默认查询出来是Object类型
        List<Long> shareIdList = shareFileService.listObjs(lqw, shareId -> (Long) shareId);
        return shareIdList;
    }

    /**
     * 执行文件下载
     * @param context
     */
    private void doDownload(ShareFileDownloadContext context) {
        FileDownloadContext fileDownloadContext = new FileDownloadContext();
        fileDownloadContext.setFileId(context.getFileId());
        fileDownloadContext.setResponse(context.getResponse());
        fileDownloadContext.setUserId(context.getUserId());
        userFileService.downloadWithoutCheckUser(fileDownloadContext);
    }

    /**
     * 校验文件ID是否在分享列表中
     * @param shareId
     * @param fileIdList
     */
    private void checkFileIdIsOnShareStatus(Long shareId, List<Long> fileIdList) {
        checkFileIdIsOnShareStatusAndGetAllShareUserFiles(shareId, fileIdList);
    }

    /**
     * 校验文件ID是在分享的文件列表中
     * @param shareId
     * @param fileIdList
     * @return 该分享的所有文件列表
     */
    private List<UserFileVO> checkFileIdIsOnShareStatusAndGetAllShareUserFiles(Long shareId, List<Long> fileIdList) {
        List<Long> shareFileIdList = getShareFileIdList(shareId);
        if (CollectionUtils.isEmpty(shareFileIdList)) {
            return Lists.newArrayList();
        }
        List<UserFile> allShareRecords = userFileService.findAllFileRecordsByFileIdList(shareFileIdList, DelFlagEnum.NO);
        if (CollectionUtils.isEmpty(allShareRecords)) {
            return Lists.newArrayList();
        }
        // 当前分享的所有文件（使用递归查询出了其中的所有子文件）
        List<Long> allShareFileIdList = allShareRecords.stream().map(UserFile::getFileId).collect(Collectors.toList());
        if (!allShareFileIdList.containsAll(fileIdList)) {
            throw new BusinessException(ResponseCode.SHARE_FILE_MISS);
        }
        List<UserFileVO> userFileVOList = allShareRecords.stream()
                .map(userFile -> fileConverter.userFile2UserFileVO(userFile))
                .collect(Collectors.toList());
        return userFileVOList;
    }

    /**
     * 查询分享者的简单信息
     * @param context
     */
    private void assembleShareSimpleUserInfo(QueryShareSimpleDetailContext context) {
        User record = userService.getById(context.getRecord().getCreateUser());
        if (Objects.isNull(record)) {
            throw new BusinessException("查询用户信息失败");
        }
        ShareUserInfoVO shareUserInfoVO = new ShareUserInfoVO();
        shareUserInfoVO.setUserId(record.getUserId());
        shareUserInfoVO.setUsername(encryptUsername(record.getUsername()));

        context.getVo().setShareUserInfoVO(shareUserInfoVO);
    }

    /**
     * 查询分享者的信息
     * @param context
     */
    private void assembleShareUserInfo(QueryShareDetailContext context) {
        User record = userService.getById(context.getRecord().getCreateUser());
        if (Objects.isNull(record)) {
            throw new BusinessException("查询用户信息失败");
        }
        ShareUserInfoVO shareUserInfoVO = new ShareUserInfoVO();
        shareUserInfoVO.setUserId(record.getUserId());
        shareUserInfoVO.setUsername(encryptUsername(record.getUsername()));

        context.getVo().setShareUserInfoVO(shareUserInfoVO);
    }

    /**
     * 加密用户名称
     * @param username
     * @return
     */
    private String encryptUsername(String username) {
        StringBuffer stringBuffer = new StringBuffer(username);
        stringBuffer.replace(CommonConstants.TWO_INT,
                username.length(),
                CommonConstants.COMMON_ENCRYPT_STR);
        return stringBuffer.toString();
    }

    /**
     * 查询分享的文件列表
     * @param context
     */
    private void assembleShareFilesInfo(QueryShareDetailContext context) {
        // 查询分享对应的文件ID集合
        List<Long> fileIdList = getShareFileIdList(context.getShareId());

        // 查询用户文件列表
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setFileIdList(fileIdList);
        queryFileListContext.setUserId(context.getRecord().getCreateUser());
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        List<UserFileVO> userFileVOList = userFileService.getFileList(queryFileListContext);

        context.getVo().setUserFileVOList(userFileVOList);
    }

    /**
     * 查询分享对应的文件ID集合
     * @param shareId
     * @return
     */
    private List<Long> getShareFileIdList(Long shareId) {
        if (Objects.isNull(shareId)) {
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<ShareFile> lqw = new LambdaQueryWrapper<>();
        lqw.select(ShareFile::getFileId);
        lqw.eq(ShareFile::getShareId, shareId);
        // tip：只查询其中一个字段，可以使用listObjs()方法
        List<Long> fileIdList = shareFileService.listObjs(lqw, fileId -> (Long)fileId);
        return fileIdList;
    }

    /**
     * 校验分享的状态是否正常
     * @param shareId
     * @return
     */
    private Share checkShareStatus(Long shareId) {
        Share record = getById(shareId);
        if (Objects.isNull(record)) {
            throw new BusinessException(ResponseCode.SHARE_CANCELLED);
        }
        if (Objects.equals(ShareStatusEnum.FILE_DELETED.getCode(), record.getShareStatus())) {
            throw new BusinessException(ResponseCode.SHARE_FILE_MISS);
        }
        if (!Objects.equals(ShareDayTypeEnum.PERMANENT_VALIDITY.getCode(), record.getShareDayType()) &&
                record.getShareEndTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResponseCode.SHARE_EXPIRE);
        }
        return record;
    }

    /**
     * 保存分享和对应文件的关联关系
     * @param context
     */
    private void saveShareFile(CreateShareUrlContext context) {
        SaveShareFilesContext saveShareFilesContext = new SaveShareFilesContext();
        saveShareFilesContext.setShareId(context.getRecord().getShareId());
        saveShareFilesContext.setShareFileIdList(context.getShareFileIdList());
        saveShareFilesContext.setUserId(context.getUserId());
        shareFileService.saveShareFiles(saveShareFilesContext);
    }

    /**
     * 拼装分享实体，保存到数据库
     * @param context
     */
    private void saveShare(CreateShareUrlContext context) {
        Share share = new Share();
        share.setShareId(IdUtil.get());
        share.setShareName(context.getShareName());
        share.setShareType(context.getShareType());
        share.setShareDayType(context.getShareDayType());

        Integer shareDay = ShareDayTypeEnum.getShareDayByCode(context.getShareDayType());
        if (Objects.equals(shareDay, CommonConstants.MINUS_ONE_INT)) {
            throw new BusinessException("分享天数非法");
        }
        share.setShareDay(shareDay);
        share.setShareEndTime(LocalDateTime.now().plusDays(shareDay));
        share.setShareUrl(createShareUrl(share.getShareId()));
        share.setShareCode(createShareCode());
        share.setShareStatus(ShareStatusEnum.NORMAL.getCode());
        share.setCreateUser(context.getUserId());
        if (!this.save(share)) {
            throw new BusinessException("保存分享信息失败");
        }
        context.setRecord(share);
    }

    /**
     * 创建分享码
     * 4位字母
     * @return
     */
    private String createShareCode() {
        return RandomStringUtils.randomAlphabetic(4);
    }

    /**
     * 创建分享链接
     * @param shareId
     * @return
     */
    private String createShareUrl(Long shareId) {
        if (Objects.isNull(shareId)) {
            throw new BusinessException("分享的ID不能为空");
        }
        String sharePrefix = config.getSharePrefix();
        if (sharePrefix.lastIndexOf(CommonConstants.SLASH_STR) == CommonConstants.MINUS_ONE_INT) {
            sharePrefix += CommonConstants.SLASH_STR;
        }
        // 这里的shareId需要加密，但是使用IdUtil进行加密生成的字符串中有/、+这些符号，在URL中有特殊含义，需要转化成unicode表示
        return sharePrefix + URLEncoder.encode(IdUtil.encrypt(shareId));
    }
}




