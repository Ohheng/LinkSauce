package com.ohh.linksauceapibackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Ohh
 * @Desctription: 用户绑定电子邮件请求
 * @Date: 2024-05-17 23:45
 * @Version: 1.0
 */
@Data
public class UserBindEmailRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String emailAccount;

    private String captcha;
}
