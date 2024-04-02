package com.ohh.linksauceinterface.controller;

import com.ohh.linksauceinterface.modal.User;
import org.springframework.web.bind.annotation.*;

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
    public String getUsernameByPost(@RequestBody User user) {
        return "POST 你的用户名是" + user.getUsername();
    }


}
