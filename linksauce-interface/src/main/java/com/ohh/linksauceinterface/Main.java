package com.ohh.linksauceinterface;

import com.ohh.linksauceinterface.client.linkSauceClient;
import com.ohh.linksauceinterface.modal.User;

public class Main {
    public static void main(String[] args) {
        String accessKey = "ohh";
        String secretKey = "abcdefgh";
        linkSauceClient linkSauceClient = new linkSauceClient(accessKey, secretKey);
        String result1 = linkSauceClient.getNameByGet("Ohh");
        String result2 = linkSauceClient.getNameByPost("Ohh");
        User user = new User();
        user.setUsername("OhhUser");
        String result3 = linkSauceClient.getUsernameByPost(user);
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
    }
}
