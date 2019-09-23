package com.warchaser.networklib.util;

/**
 * 用于多请求合并操作时遇到网络Api返回code异常的Exception
 * */
final public class ErrorCodeException extends Exception {

    private int mErrorCode = 0;
    private Object mObject;

    public ErrorCodeException(String message){
        super(message);
    }

    public ErrorCodeException(String message, int errorCode){
        super(message);
        setErrorCode(errorCode);
    }

    public ErrorCodeException(String message, int errorCode, Object object){
        super(message);
        setErrorCode(errorCode);
        setObject(object);
    }

    public ErrorCodeException(String message, Throwable cause){
        super(message,cause);
    }


    public int getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(int errorCode) {
        this.mErrorCode = errorCode;
    }

    public Object getObject() {
        return mObject;
    }

    public void setObject(Object object) {
        this.mObject = object;
    }
}
