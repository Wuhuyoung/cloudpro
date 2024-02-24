package com.cloud.pro.web.validator;

import com.cloud.pro.core.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * 统一的参数校验器
 * 配置参数校验的模式为快速失败，只要有一处入参检测到不符合就返回失败，其他的参数都不再检测
 */
@Configuration
@Slf4j
public class WebValidatorConfig {
    private static final String FAIL_FAST_KEY = "hibernate.validator.fail_fast";

    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(cloudProValidator());
        log.info("The hibernate validator is loaded successfully!");
        return processor;
    }

    /**
     * 构造项目的方法参数校验器
     * @return
     */
    private Validator cloudProValidator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty(FAIL_FAST_KEY, CommonConstants.TRUE_STR) // 模式为快速失败
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        return validator;
    }
}
