package com.ohh.linksauceapibackend.controller;

import com.ohh.linksauceapibackend.common.BaseResponse;
import com.ohh.linksauceapibackend.common.ErrorCode;
import com.ohh.linksauceapibackend.common.ResultUtils;
import com.ohh.linksauceapibackend.exception.BusinessException;
import com.ohh.linksauceapibackend.model.dto.UserBindEmailRequest;
import com.ohh.linksauceapibackend.model.dto.UserEmailLoginRequest;
import com.ohh.linksauceapibackend.model.dto.UserLoginRequest;
import com.ohh.linksauceapibackend.model.dto.UserRegisterRequest;
import com.ohh.linksauceapibackend.model.vo.UserVO;
import com.ohh.linksauceapibackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.ohh.linksauceapibackend.constant.EmailConstant.CAPTCHA_CACHE_KEY;

/**
 * @Author: Ohh
 * @Desctription: 用户接口
 * @Date: 2024-05-09 16:11
 * @Version: 1.0
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册表
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(result);
    }


    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @param request          请求
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {

        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UserVO user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);

    }

    /**
     * 用户邮箱登录
     * @param userEmailLoginRequest 用户登录请求
     * @param request
     * @return
     */
    @PostMapping("/email/login")
    public BaseResponse<UserVO> userEmailLogin(@RequestBody UserEmailLoginRequest userEmailLoginRequest,HttpServletRequest request){
        if (userEmailLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UserVO user = userService.userEmailLogin(userEmailLoginRequest,request);
        redisTemplate.delete(CAPTCHA_CACHE_KEY + userEmailLoginRequest.getEmailAccount());
        return ResultUtils.success(user);
    }

    /**
     * 用户绑定邮箱
     * @param userBindEmailRequest
     * @param request
     * @return
     */
    @PostMapping("/bind/login")
    public BaseResponse<UserVO> userBindEmail(@RequestBody UserBindEmailRequest userBindEmailRequest,HttpServletRequest request){
        if (userBindEmailRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO user = userService.userBindEmail(userBindEmailRequest,request);
        return ResultUtils.success(user);
    }

}
