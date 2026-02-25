package com.example.healthdiet.common;

import lombok.Data;

/**
 * 统一接口返回格式。
 */
@Data
public class ApiResponse<T> {

    /**
     * 业务编码：0 表示成功，其他表示失败。
     */
    private int code;

    /**
     * 提示信息。
     */
    private String message;

    /**
     * 具体数据。
     */
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.setCode(0);
        r.setMessage("success");
        r.setData(data);
        return r;
    }

    public static <T> ApiResponse<T> fail(String message) {
        ApiResponse<T> r = new ApiResponse<>();
        r.setCode(-1);
        r.setMessage(message);
        return r;
    }
}

