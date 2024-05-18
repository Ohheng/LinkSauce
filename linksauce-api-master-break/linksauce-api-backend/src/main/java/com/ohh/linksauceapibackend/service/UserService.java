package com.ohh.linksauceapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ohh.linksauceapibackend.model.dto.UserBindEmailRequest;
import com.ohh.linksauceapibackend.model.dto.UserEmailLoginRequest;
import com.ohh.linksauceapibackend.model.dto.UserRegisterRequest;
import com.ohh.linksauceapibackend.model.entity.User;
import com.ohh.linksauceapibackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Ohh
 * @Desctription: 用户服务
 * @Date: 2024-05-09 16:20
 * @Version: 1.0
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userRegisterRequest 用户注册请求
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 添加钱包余额
     * @param userId 用户id
     * @param addPoints 添加点
     * @return
     */
    boolean addWalletBalance(Long userId, Integer addPoints);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      请求
     * @return 脱敏后的用户信息
     */
    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户邮箱登录
     * @param userEmailLoginRequest
     * @param request
     * @return
     */
    UserVO userEmailLogin(UserEmailLoginRequest userEmailLoginRequest, HttpServletRequest request);

    /**
     * 用户绑定邮箱
     * @param userBindEmailRequest
     * @param request
     * @return
     */
    UserVO userBindEmail(UserBindEmailRequest userBindEmailRequest, HttpServletRequest request);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    UserVO getLoginUser(HttpServletRequest request);
}
