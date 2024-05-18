package com.ohh.linksauceapibackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Ohh
 * @Desctription: 用户登录请求体
 * @Date: 2024-05-09 17:55
 * @Version: 1.0
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;
}
