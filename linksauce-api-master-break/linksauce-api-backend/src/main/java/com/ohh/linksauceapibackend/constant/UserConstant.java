package com.ohh.linksauceapibackend.constant;

/**
 * @Author: Ohh
 * @Desctription: 用户常量
 * @Date: 2024-05-09 16:45
 * @Version: 1.0
 */
public interface UserConstant {
    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 系统用户 id（虚拟用户）
     */
    long SYSTEM_USER_ID = 0;

    //  region 权限

    /**
     * 默认权限
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员权限
     */
    String ADMIN_ROLE = "admin";


    /**
     * 盐值，混淆密码
     */
    String SALT = "ohh";
    /**
     * ak/sk 混淆
     */
    String VOUCHER = "accessKey_secretKey";
}
