package com.cloud.pro.server.common.config;

import com.cloud.pro.server.common.interceptor.BloomFilterInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

/**
 * 项目拦截器配置类
 */
@Configuration
@Slf4j
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private List<BloomFilterInterceptor> interceptorList;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (CollectionUtils.isNotEmpty(interceptorList)) {
            interceptorList.stream().forEach(interceptor -> {
                registry.addInterceptor(interceptor)
                        .addPathPatterns(interceptor.getPathPatterns())
                        .excludePathPatterns(interceptor.getExcludePatterns());
                log.info("add bloomFilterInterceptor named {} finish.", interceptor.getName());
            });
        }
    }
}
