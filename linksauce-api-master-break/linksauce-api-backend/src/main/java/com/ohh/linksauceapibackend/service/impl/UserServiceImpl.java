package com.ohh.linksauceapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ohh.linksauceapibackend.common.ErrorCode;
import com.ohh.linksauceapibackend.exception.BusinessException;
import com.ohh.linksauceapibackend.mapper.UserMapper;
import com.ohh.linksauceapibackend.model.dto.UserBindEmailRequest;
import com.ohh.linksauceapibackend.model.dto.UserEmailLoginRequest;
import com.ohh.linksauceapibackend.model.dto.UserRegisterRequest;
import com.ohh.linksauceapibackend.model.entity.User;
import com.ohh.linksauceapibackend.model.enums.UserAccountStatusEnum;
import com.ohh.linksauceapibackend.model.vo.UserVO;
import com.ohh.linksauceapibackend.service.UserService;
import com.ohh.linksauceapibackend.utils.RedissonLockUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Random;
import java.util.regex.Pattern;

import static com.ohh.linksauceapibackend.constant.EmailConstant.CAPTCHA_CACHE_KEY;
import static com.ohh.linksauceapibackend.constant.UserConstant.*;

/**
 * @Author: Ohh
 * @Desctription: 用户服务实现类
 * @Date: 2024-05-09 16:25
 * @Version: 1.0
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedissonLockUtil redissonLockUtil;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return long
     */
    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String userName = userRegisterRequest.getUserName();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String invitationCode = userRegisterRequest.getInvitationCode();

        // 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userName.length() > 40) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名过长");
        }
        if (userName.length() < 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        // 账户不包括特殊字符
        // 匹配由数字、小写字母、大写字母组成的字符串,且字符串的长度至少为1个字符
        String pattern = "[0-9a-zA-Z]+";
        if (!userAccount.matches(pattern)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号由数字、小写字母、大写字母组成");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        String redissonLock = ("userRegister_" + userAccount).intern();

        return redissonLockUtil.redissonDistributedLocks(redissonLock, () -> {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            User invitationCodeUser = null;
            if (StringUtils.isNotBlank(invitationCode)) {
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.eq(User::getInvitationCode, invitationCode);
                // 可能出现重复invitationCode,查出的不是一条
                invitationCodeUser = this.getOne(userLambdaQueryWrapper);
                if (invitationCodeUser == null) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "该邀请码无效");
                }
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // ak/sk
            String accessKey = DigestUtils.md5DigestAsHex((userAccount + SALT + VOUCHER).getBytes());
            String secretKey = DigestUtils.md5DigestAsHex((SALT + VOUCHER + userAccount).getBytes());

            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserName(userName);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            // 如果邀请码用户不为空，将用户余额设置为100，并将100添加到邀请码用户的钱包余额中
            if (invitationCodeUser != null) {
                user.setBalance(100);
                this.addWalletBalance(invitationCodeUser.getId(), 100);
            }
            // 设置用户邀请码为8位随机字符串
            user.setInvitationCode(generateRandomString(8));
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }, "注册账号失败");

    }


    @Override
    public boolean addWalletBalance(Long userId, Integer addPoints) {
        // 创建一个LambdaUpdateWrapper对象来更新用户的余额
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        // 添加一个相等条件来更新具有指定userId的用户
        userLambdaUpdateWrapper.eq(User::getId, userId);
        // 设置SQL语句以更新用户的余额，通过添加指定的addPoints
        userLambdaUpdateWrapper.setSql("balance = balance + " + addPoints);
        return this.update(userLambdaUpdateWrapper);
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户帐户
     * @param userPassword 用户密码
     * @param request      请求
     * @return
     */
    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号过短");
        }
        if (userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        // 账户不包含特殊字符
        // 匹配由数字、小写字母、大写字母组成的字符串,且字符串的长度至少为1个字符
        String pattern = "[0-9a-zA-Z]+";
        if (!userAccount.matches(pattern)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号需由数字、小写字母、大写字母组成");
        }

        // 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);

        return null;
    }

    /**
     * 用户邮箱登录
     * @param userEmailLoginRequest
     * @param request
     * @return
     */
    @Override
    public UserVO userEmailLogin(UserEmailLoginRequest userEmailLoginRequest, HttpServletRequest request) {
        String emailAccount = userEmailLoginRequest.getEmailAccount();
        String captcha = userEmailLoginRequest.getCaptcha();

        if (StringUtils.isAnyBlank(emailAccount, captcha)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailPattern, emailAccount)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不合法的邮箱地址！");
        }
        String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + emailAccount);
        if (StringUtils.isBlank(cacheCaptcha)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"验证码已过期,请重新获取!");
        }
        captcha = captcha.trim();
        if(!cacheCaptcha.equals(captcha)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"验证码错误,请重新输入!");
        }
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userEmail", emailAccount);
        User user = userMapper.selectOne(queryWrapper);

        // 用户不存在
        if (user == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"该邮箱未绑定账号，请先绑定账号！");
        }

        if(user.getStatus().equals(UserAccountStatusEnum.BAN.getValue())){
            throw new BusinessException(ErrorCode.PROHIBITED,"该账号已被封禁，请联系管理员！");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, userVO);
        return userVO;
    }

    /**
     * 用户绑定邮箱
     * @param userBindEmailRequest
     * @param request
     * @return
     */
    @Override
    public UserVO userBindEmail(UserBindEmailRequest userBindEmailRequest, HttpServletRequest request) {

        String emailAccount = userBindEmailRequest.getEmailAccount();
        String captcha = userBindEmailRequest.getCaptcha();

        if (StringUtils.isAnyBlank(emailAccount, captcha)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailPattern, emailAccount)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不合法的邮箱地址！");
        }
        String cacheCaptcha = redisTemplate.opsForValue().get(CAPTCHA_CACHE_KEY + emailAccount);
        if (StringUtils.isBlank(cacheCaptcha)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"验证码已过期,请重新获取!");
        }
        captcha = captcha.trim();
        if(!cacheCaptcha.equals(captcha)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"验证码错误,请重新输入!");
        }
        // 查询用户是否已经绑定邮箱
        UserVO loginUser = this.getLoginUser(request);

        if (loginUser.getEmail() != null && emailAccount.equals(loginUser.getEmail())){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"该账号已绑定此邮箱，请更换新邮箱地址！");
        }

        // 查询邮箱是否已绑定
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", emailAccount);
        User user = this.getOne(queryWrapper);
        if (user != null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"该邮箱已被绑定账号，请更换新邮箱地址！");
        }

        user = new User();
        user.setId(loginUser.getId());
        user.setEmail(emailAccount);
        boolean bindEmailResult = this.updateById(user);
        if (!bindEmailResult){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"绑定邮箱失败，请稍后重试！");
        }
        loginUser.setEmail(emailAccount);
        return loginUser;
    }

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @Override
    public UserVO getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) userObj;
        if (currentUser == null || currentUser.getId() == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        Long userId = currentUser.getId();
        User user = this.getById(userId);
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (user.getStatus().equals(UserAccountStatusEnum.BAN.getValue())){
            throw new BusinessException(ErrorCode.PROHIBITED,"账号已被封禁，请联系管理员！");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }

    /**
     * 生成随机字符串
     * @param length 长度
     * @return
     */
    public String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }


}
