package com.ohh.linksaucecommon.service;

import com.ohh.linksaucecommon.model.entity.User;

/**
 * 用户服务
 *
 * @author ohh
 */
public interface InnerUserService {

    /**
     * 数据库中查是否已分配给用户密钥
     *
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);

}
