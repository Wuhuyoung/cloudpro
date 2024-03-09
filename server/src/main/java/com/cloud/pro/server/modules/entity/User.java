package com.cloud.pro.server.modules.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import lombok.Data;

/**
 * 用户信息表
 * @TableName cloud_pro_user
 */
@TableName(value ="cloud_pro_user")
@Data
public class User implements Serializable {
    /**
     * 用户id
     */
    @TableId
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 随机盐值
     */
    private String salt;

    /**
     * 密保问题
     */
    private String question;

    /**
     * 密保答案
     */
    private String answer;

    /**
     * 创建时间
     */
    private LocalDate createTime;

    /**
     * 更新时间
     */
    private LocalDate updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}