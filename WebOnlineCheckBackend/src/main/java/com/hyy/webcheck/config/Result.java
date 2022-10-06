package com.hyy.webcheck.config;

import lombok.Data;

@Data
public class Result<T> {
    int status ;//状态码，200成功
    T data;
    String message;

    public Result(int _status,String _message,T _data){
        status = _status;
        message = _message;
        data = _data;
    }
}
