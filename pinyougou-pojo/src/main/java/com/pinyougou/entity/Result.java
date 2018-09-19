package com.pinyougou.entity;

import java.io.Serializable;

//这里的序列化接口是为了dubbo的消息传递
public class Result implements Serializable {

    private Boolean success;
    private String message;

    public Result() {
    }

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
