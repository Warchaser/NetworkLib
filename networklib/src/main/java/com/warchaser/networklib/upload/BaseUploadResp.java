package com.warchaser.networklib.upload;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

public class BaseUploadResp<T> {

    private int statusCode;

    private String errorMsg;

    private T data;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
