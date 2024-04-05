package com.ohh.project.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通过id发送请求
 *
 * @author ohh
 */
@Data
public class IdRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}