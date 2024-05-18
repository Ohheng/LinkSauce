package com.ohh.linksauceapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ohh.linksauceapibackend.model.dto.user.*;
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
     * 减少钱包余额
     * @param userId
     * @param reduceScore
     * @return
     */
    boolean reduceWalletBalance(Long userId, Integer reduceScore);

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

    /**
     * 用户邮箱注册
     * @param userEmailRegisterRequest
     * @return
     */
    long userEmailRegister(UserEmailRegisterRequest userEmailRegisterRequest);

    /**
     * 是否为管理员
     *
     * @param request 请求
     * @return boolean
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是游客
     *
     * @param request 要求
     * @return
     */
    User isTourist(HttpServletRequest request);

    /**
     * 用户注销
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 校验用户
     * @param user
     * @param add
     */
    void validUser(User user, boolean add);

    /**
     * 更新凭证
     * @param loginUser
     * @return
     */
    UserVO updateVoucher(User loginUser);

    /**
     * 用户取消绑定电子邮件
     * @param userUnBindEmailRequest
     * @param request
     * @return
     */
    UserVO userUnBindEmail(UserUnBindEmailRequest userUnBindEmailRequest, HttpServletRequest request);
}
