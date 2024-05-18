package com.ohh.linksauceapibackend.constant;

/**
 * @Author: Ohh
 * @Desctription: 电子邮件常量
 * @Date: 2024-05-17 23:20
 * @Version: 1.0
 */
public interface EmailConstant {
    /**
     * 电子邮件html内容路径 resources目录下
     */
    String EMAIL_HTML_CONTENT_PATH = "email.html";

    /**
     * 电子邮件html支付成功路径
     */
    String EMAIL_HTML_PAY_SUCCESS_PATH = "pay.html";

    /**
     * captcha缓存键
     */
    String CAPTCHA_CACHE_KEY = "api:captcha:";

    /**
     * 电子邮件主题
     */
    String EMAIL_SUBJECT = "验证码邮件";

    /**
     * 电子邮件标题
     */
    String EMAIL_TITLE = "LinkSauce-API 接口开放平台";

    /**
     * 电子邮件标题英语
     */
    String EMAIL_TITLE_ENGLISH = "LinkSauce-API Open Interface Platform";

    /**
     * 平台负责人
     */
    String PLATFORM_RESPONSIBLE_PERSON = "Ohh";

    /**
     * todo 平台地址
     */
    String PLATFORM_ADDRESS = "<a href='https://api.qimuu.icu/'>请联系我们</a>";

    String PATH_ADDRESS = "'https://api.qimuu.icu/'";
}
