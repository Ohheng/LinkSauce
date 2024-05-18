package com.ohh.linksauceapibackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Ohh
 * @Desctription: 用户登录请求体
 * @Date: 2024-05-17 23:09
 * @Version: 1.0
 */
@Data
public class UserEmailLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String emailAccount;

    private String captcha;
}
