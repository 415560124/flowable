package com.rhy.flowable.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommonRes<T> {
    private Integer status;
    private String message;
    private T data;

    public static <T> CommonRes ok(T data){
        return new CommonRes().setStatus(200).setMessage("成功").setData(data);
    }
}
