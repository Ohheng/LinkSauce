package com.ohh.linksauceapibackend.controller;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.ohh.linksauceapibackend.annotation.AuthCheck;
import com.ohh.linksauceapibackend.common.*;
import com.ohh.linksauceapibackend.config.EmailConfig;
import com.ohh.linksauceapibackend.exception.BusinessException;
import com.ohh.linksauceapibackend.model.dto.user.*;
import com.ohh.linksauceapibackend.model.entity.User;
import com.ohh.linksauceapibackend.model.enums.UserAccountStatusEnum;
import com.ohh.linksauceapibackend.model.vo.UserVO;
import com.ohh.linksauceapibackend.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.ohh.linksauceapibackend.constant.EmailConstant.*;
import static com.ohh.linksauceapibackend.constant.UserConstant.ADMIN_ROLE;
import static com.ohh.linksauceapibackend.utils.EmailUtil.buildEmailContent;

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

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private EmailConfig emailConfig;

    // region 登录相关

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
     *
     * @param userEmailLoginRequest 用户登录请求
     * @param request
     * @return
     */
    @PostMapping("/email/login")
    public BaseResponse<UserVO> userEmailLogin(@RequestBody UserEmailLoginRequest userEmailLoginRequest, HttpServletRequest request) {
        if (userEmailLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UserVO user = userService.userEmailLogin(userEmailLoginRequest, request);
        redisTemplate.delete(CAPTCHA_CACHE_KEY + userEmailLoginRequest.getEmailAccount());
        return ResultUtils.success(user);
    }

    /**
     * 用户绑定邮箱
     *
     * @param userBindEmailRequest
     * @param request
     * @return
     */
    @PostMapping("/bind/login")
    public BaseResponse<UserVO> userBindEmail(@RequestBody UserBindEmailRequest userBindEmailRequest, HttpServletRequest request) {
        if (userBindEmailRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO user = userService.userBindEmail(userBindEmailRequest, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户解绑邮箱
     * @param userUnBindEmailRequest
     * @param request
     * @return
     */
    @PostMapping("/unbindEmail")
    public BaseResponse<UserVO> userUnBindEmail(@RequestBody UserUnBindEmailRequest userUnBindEmailRequest, HttpServletRequest request) {
        if (userUnBindEmailRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO user = userService.userUnBindEmail(userUnBindEmailRequest, request);
        redisTemplate.delete(CAPTCHA_CACHE_KEY + userUnBindEmailRequest.getEmailAccount());
        return ResultUtils.success(user);
    }

    /**
     * 用户邮箱注册
     *
     * @param userEmailRegisterRequest
     * @return
     */
    @PostMapping("/email/register")
    public BaseResponse<Long> userEmailRegister(@RequestBody UserEmailRegisterRequest userEmailRegisterRequest) {
        if (userEmailRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userEmailRegister(userEmailRegisterRequest);
        redisTemplate.delete(CAPTCHA_CACHE_KEY + userEmailRegisterRequest.getEmailAccount());
        return ResultUtils.success(result);
    }


    /**
     * 获取验证码
     *
     * @param emailAccount
     * @return
     */
    @GetMapping("/getCaptcha")
    public BaseResponse<Boolean> getCaptcha(String emailAccount) {
        if (StringUtils.isBlank(emailAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailPattern, emailAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不合法的邮箱地址！");
        }

        String captcha = RandomUtil.randomNumbers(6);
        try {
            sendEmail(emailAccount, captcha);
            redisTemplate.opsForValue().set(CAPTCHA_CACHE_KEY + emailAccount, captcha, 5, TimeUnit.MINUTES);
            return ResultUtils.success(true);
        } catch (Exception e) {
            log.error("【发送验证码失败】" + e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "发送验证码失败！");
        }

    }

    private void sendEmail(String emailAccount, String captcha) throws MessagingException {
        // 创建MimeMessage对象
        MimeMessage message = mailSender.createMimeMessage();
        // 邮箱发送内容组成
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        // 设置主题
        helper.setSubject(EMAIL_SUBJECT);
        // 设置正文
        helper.setText(buildEmailContent(EMAIL_HTML_CONTENT_PATH, captcha), true);
        // 设置收件人
        helper.setTo(emailAccount);
        // 设置发件人
        helper.setFrom(EMAIL_TITLE + '<' + emailConfig.getEmailFrom() + '>');
        // 发送邮件
        mailSender.send(message);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        UserVO user = userService.getLoginUser(request);
        UserVO userVO = new UserVO();

        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    // endregion

    // region 增删改查

    /**
     * 添加用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        userService.validUser(user, true);

        boolean result = userService.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (ObjectUtils.anyNull(deleteRequest, deleteRequest.getId()) || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.removeById(deleteRequest.getId()));
    }


    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<UserVO> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (ObjectUtils.anyNull(userUpdateRequest, userUpdateRequest.getId()) || userUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 管理员操作
        boolean adminOperation = ObjectUtils.isNotEmpty(userUpdateRequest.getBalance())
                || StringUtils.isNoneBlank(userUpdateRequest.getUserRole())
                || StringUtils.isNoneBlank(userUpdateRequest.getUserPassword());
        // 校验是否登录
        UserVO loginUser = userService.getLoginUser(request);
        // 处理管理员事务，不是管理员抛异常
        if (adminOperation && !loginUser.getUserRole().equals(ADMIN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if (!loginUser.getUserRole().equals(ADMIN_ROLE) && !userUpdateRequest.getId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "只有本人或管理员才能修改");
        }

        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        // 参数校验
        userService.validUser(user, false);

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getId, user.getId());

        boolean result = userService.update(user, userLambdaQueryWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新失败");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userService.getById(user.getId()), userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 根据 id 获取用户
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<UserVO> getUserById(int id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<UserVO>> getUserList(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User userQuery = new User();
        BeanUtils.copyProperties(userQueryRequest, userQuery);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        List<User> userList = userService.list(queryWrapper);
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    /**
     * 获取用户分页列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserVO>> listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        User userQuery = new User();
        if (userQueryRequest != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        BeanUtils.copyProperties(userQueryRequest, userQuery);
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String gender = userQueryRequest.getGender();
        String userRole = userQueryRequest.getUserRole();
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName)
                .eq(StringUtils.isNotBlank(userAccount), "userAccount", userAccount)
                .eq(StringUtils.isNotBlank(gender), "gender", gender)
                .eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        Page<User> userPage = userService.page(new Page<>(current, pageSize), queryWrapper);
        PageDTO<UserVO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);

    }

    @PostMapping("/update/voucher")
    public BaseResponse<UserVO> updateVoucher(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(loginUser, user);
        UserVO userVO = userService.updateVoucher(user);
        return ResultUtils.success(userVO);
    }

    /**
     * 通过邀请码获取用户
     *
     * @param invitationCode
     * @return
     */
    @PostMapping("/get/invitationCode")
    public BaseResponse<UserVO> getUserByInvitationCode(String invitationCode) {
        if (StringUtils.isBlank(invitationCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getInvitationCode, invitationCode);
        User invitationCodeUser = userService.getOne(userLambdaQueryWrapper);
        if (invitationCodeUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "邀请码不存在");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(invitationCodeUser, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 解封
     *
     * @param idRequest
     * @return
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/normal")
    public BaseResponse<Boolean> normalUser(@RequestBody IdRequest idRequest) {
        if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        user.setStatus(UserAccountStatusEnum.NORMAL.getValue());
        return ResultUtils.success(userService.updateById(user));
    }

    /**
     * 封号
     * @param idRequest
     * @param request
     * @return
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/ban")
    public BaseResponse<Boolean> banUser(@RequestBody IdRequest idRequest,HttpServletRequest request){
        if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        user.setStatus(UserAccountStatusEnum.BAN.getValue());
        return ResultUtils.success(userService.updateById(user));
    }

    // endregion
}