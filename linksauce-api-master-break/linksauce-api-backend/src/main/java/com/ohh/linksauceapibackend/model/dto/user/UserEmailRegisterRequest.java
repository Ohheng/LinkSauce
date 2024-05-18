package com.ohh.linksauceapibackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Ohh
 * @Desctription: 用户注册请求体
 * @Date: 2024-05-17 23:10
 * @Version: 1.0
 */
@Data
public class UserEmailRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String emailAccount;

    private String captcha;

    private String userName;

    private String invitationCode;

    private String agreeToAnAgreement;
}
