package com.ohh.linksauceinterface.controller;

import com.ohh.linksauceinterface.modal.User;
import com.ohh.linksauceinterface.utils.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 名称API
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/")
    public String getNameByGet(String name) {
        return "GET 你的名字是" + name;
    }

    @PostMapping("/")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request) {
        String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = request.getHeader("body");
        // todo 实际开发应去数据库中查看是否已分配给用户
        if (!accessKey.equals("ohh")) {
            throw new RuntimeException("无权限访问");
        }
        if (Long.parseLong(nonce) > 10000) {
            throw new RuntimeException("无权限访问");
        }
        // todo 时间和当前时间不能超过5分钟
        // if (timestamp){
        //
        // }

        // todo 实际情况是从数据库中查secretKey
        String serverSign = SignUtils.genSign(body, "abcdefgh");
        if (!sign.equals(serverSign)) {
            throw new RuntimeException("签名错误");
        }

        return "POST 你的用户名是" + user.getUsername();
    }


}
