package com.cloud.pro.swagger2;

import com.cloud.pro.core.constants.CommonConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * swagger2配置属性实体
 */
@Component
@ConfigurationProperties(prefix = "swagger2")
@Data
public class Swagger2ConfigProperties {
    private boolean show = true;

    private String groupName = "cloud-pro";

    private String basePackage = CommonConstants.BASE_COMPONENT_SCAN_PATH;

    private String title = "cloud-pro-server";

    private String description = "cloud-pro-server";

    private String termsOfServiceUrl = "http://127.0.0.1:${server.port}";

    private String contactName = "han";

    private String contactUrl = "";

    private String contactEmail = "1138841120@qq.com";

    private String version = "1.0";
}
