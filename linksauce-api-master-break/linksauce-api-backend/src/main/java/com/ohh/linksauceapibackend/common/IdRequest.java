package com.ohh.linksauceapibackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Ohh
 * @Desctription: id请求
 * @Date: 2024-05-09 00:14
 * @Version: 1.0
 */
@Data
public class IdRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
}
