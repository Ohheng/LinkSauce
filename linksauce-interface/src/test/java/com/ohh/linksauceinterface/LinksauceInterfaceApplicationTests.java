package com.ohh.linksauceinterface;

import com.linksauce.linksauceclientsdk.client.linkSauceClient;
import com.linksauce.linksauceclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class LinksauceInterfaceApplicationTests {

    @Resource
    private linkSauceClient linkSauceClient;

    @Test
    void contextLoads() {
        String result = linkSauceClient.getNameByGet("ohh");
        User user = new User();
        user.setUsername("ohh");
        String userNameByPost = linkSauceClient.getNameByPostWithJson(user);
        System.out.println(result);
        System.out.println(userNameByPost);
    }

}
