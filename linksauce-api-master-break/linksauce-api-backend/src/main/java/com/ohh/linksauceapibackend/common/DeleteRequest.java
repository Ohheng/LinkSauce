package com.ohh.linksauceapibackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Ohh
 * @Desctription: 删除请求
 * @Date: 2024-05-09 00:13
 * @Version: 1.0
 */
@Data
public class DeleteRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
}
