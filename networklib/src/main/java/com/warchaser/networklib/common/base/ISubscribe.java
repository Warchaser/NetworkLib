package com.warchaser.networklib.common.base;

public interface ISubscribe<T> {
    /**
     * 服务器正常返回
     * */
    void onINext(T t) throws Exception;

    /**
     *  Token失效
     * */
    void onTokenExpire();

    /**
     * Token不存在
     * */
    void onTokenNotExist();
}
