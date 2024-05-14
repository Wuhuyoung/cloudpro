package com.cloud.pro.server.common.interceptor;

import com.cloud.pro.bloom.filter.core.BloomFilter;
import com.cloud.pro.bloom.filter.core.BloomFilterManager;
import com.cloud.pro.core.exception.BusinessException;
import com.cloud.pro.core.response.ResponseCode;
import com.cloud.pro.core.utils.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 简单查询分享 布隆过滤器拦截器
 */
@Component
@Slf4j
public class ShareSimpleDetailBloomFilterInterceptor implements BloomFilterInterceptor {

    @Resource
    private BloomFilterManager manager;

    private static final String BLOOM_FILTER_NAME = "SHARE_SIMPLE_DETAIL";

    @Override
    public String getName() {
        return "ShareSimpleDetailBloomFilterInterceptor";
    }

    @Override
    public String[] getPathPatterns() {
        return ArrayUtils.toArray("/share/simple");
    }

    @Override
    public String[] getExcludePatterns() {
        return new String[0];
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String encShareId = request.getParameter("shareId");
        if (StringUtils.isBlank(encShareId)) {
            throw new BusinessException("分享ID不能为空");
        }
        BloomFilter<Long> bloomFilter = manager.getFilter(BLOOM_FILTER_NAME);
        if (Objects.isNull(bloomFilter)) {
            log.info("the bloom filter named {} is null, give up existence judgement", BLOOM_FILTER_NAME);
            return true;
        }
        Long shareId = IdUtil.decrypt(encShareId);
        boolean mightContain = bloomFilter.mightContain(shareId);
        // 可能存在，放行
        if (mightContain) {
//            log.info("the bloom filter named {} judge shareId {} mightContain pass", BLOOM_FILTER_NAME, shareId);
            return true;
        }
        log.info("the bloom filter named {} judge shareId {} mightContain fail", BLOOM_FILTER_NAME, shareId);
        throw new BusinessException(ResponseCode.SHARE_CANCELLED);
    }
}
