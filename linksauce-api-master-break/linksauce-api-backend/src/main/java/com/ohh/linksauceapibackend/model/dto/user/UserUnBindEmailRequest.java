package com.ohh.linksauceapibackend.model.dto.user;

import lombok.Data;

/**
 * @Author: Ohh
 * @Desctription: 用户取消绑定电子邮件请求
 * @Date: 2024-05-18 14:48
 * @Version: 1.0
 */
@Data
public class UserUnBindEmailRequest {
    private static final long serialVersionUID = 3191241716373120793L;

    private String emailAccount;

    private String captcha;
}
