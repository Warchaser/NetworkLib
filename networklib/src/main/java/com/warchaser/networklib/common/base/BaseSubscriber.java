package com.warchaser.networklib.common.base;

import android.net.ParseException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.warchaser.commonuitls.PackageUtil;
import com.warchaser.networklib.util.ErrorCodeException;
import com.warchaser.networklib.util.ErrorCodeUtil;
import com.warchaser.networklib.util.NLog;

import org.json.JSONException;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * 普通Http(s)请求Subscriber基类
 * */
public class BaseSubscriber<T> implements Subscriber<T> , ISubscribe<T> {

    /**
     * 返回体解析关键字
     * */
    public static final String RES_KEY_DATA = "data";
    public static final String RES_KEY_MESSAGE = "message";
    public static final String RES_KEY_STATE = "state";

    private Subscription mSubscription;
    private final String TAG = "SUBSCRIBE";
    private final String THIS = PackageUtil.getSimpleClassName(this);
    private final int REQUEST_COUNT = 2;

    private boolean mIsErrorFromNext = false;

    @Override
    public void onSubscribe(Subscription s) {
        NLog.i(TAG, THIS + "onSubScribe()");
        s.request(REQUEST_COUNT);
        mSubscription = s;
        mIsErrorFromNext = false;
    }

    @Override
    public final void onNext(T t) {
        NLog.i(TAG, THIS + "onNext()");
        try {
            mSubscription.request(REQUEST_COUNT);
            if(t instanceof JsonObject) {
                final int state = ((JsonObject) t).get(RES_KEY_STATE).getAsInt();

                if(state == ErrorCodeUtil.TOKEN_EXPIRED){
                    onTokenExpire();
                } else if(state == ErrorCodeUtil.TOKEN_NEEDED){
                    onTokenNotExist();
                } else if(state == ErrorCodeUtil.TOKEN_NOT_EXIST){
                    onTokenNotExist();
                } else {
                    onINext(t);
                }
            } else {
                onINext(t);
            }
        } catch (Exception | Error e){
            NLog.printStackTrace(TAG, e);
            mIsErrorFromNext = true;
            onError(e);
        }

    }

    @Override
    public void onINext(T t){
        NLog.i(TAG, THIS + "onINext()");
    }

    @Override
    public void onTokenExpire() {
        NLog.i(TAG, THIS + "onTokenExpire()");
    }

    @Override
    public void onTokenNotExist() {
        NLog.i(TAG, THIS + "onTokenNotExist()");
    }

    @Override
    public void onError(Throwable t) {
        NLog.e(TAG, THIS + "onError()");
        if (!mIsErrorFromNext){
            NLog.printStackTrace(TAG, t);
            if(t instanceof ErrorCodeException){
                int errorCode = ((ErrorCodeException) t).getErrorCode();
                if(errorCode == ErrorCodeUtil.TOKEN_NEEDED || errorCode == ErrorCodeUtil.TOKEN_NOT_EXIST){
                    onTokenNotExist();
                }
            }
        }
    }

    @Override
    public void onComplete() {
        NLog.i(TAG, THIS + "onComplete()");
    }

    /**
     * 取消订阅
     * */
    public final void cancelSubscribe(){
        if(mSubscription != null){
            mSubscription.cancel();
            mSubscription = null;
        }
    }

    public final String getErrorString(Throwable e){

        String result;

        //HTTP错误
        if (e instanceof HttpException) {
            result = ExceptionReason.BAD_NETWORK.typeName;
            return result;
        }

        //连接错误
        if (e instanceof ConnectException
                || e instanceof UnknownHostException
                || e instanceof SocketException) {
            result = ExceptionReason.CONNECT_ERROR.typeName;
            return result;
        }

        //连接超时
        if (e instanceof InterruptedIOException) {
            result = ExceptionReason.CONNECT_TIMEOUT.typeName;
            return result;
        }

        //解析错误
        if (e instanceof JSONException
                || e instanceof ParseException
                || e instanceof JsonParseException
                || e instanceof ClassCastException) {
            result = ExceptionReason.PARSE_ERROR.typeName;
            return result;
        }

        //未知错误
        result = ExceptionReason.UNKNOWN_ERROR.typeName;

        return result;

    }

    /**
     * 请求网络失败原因
     */
    public enum ExceptionReason {
        /**
         * 解析数据失败
         */
        PARSE_ERROR("数据解析失败"),
        /**
         * 网络问题
         */
        BAD_NETWORK("网络问题"),
        /**
         * 连接错误
         */
        CONNECT_ERROR("连接错误"),
        /**
         * 连接超时
         */
        CONNECT_TIMEOUT("连接超时"),
        /**
         * 未知错误
         */
        UNKNOWN_ERROR("未知错误");

        ExceptionReason(String str) {
            this.typeName = str;
        }

        private String typeName;

        /**
         * 根据类型的名称，返回类型的枚举实例。
         *
         * @param typeName 类型名称
         */
        public static ExceptionReason fromTypeName(String typeName) {
            for (ExceptionReason type : ExceptionReason.values()) {
                if (type.getTypeName().equals(typeName)) {
                    return type;
                }
            }
            return null;
        }

        public String getTypeName() {
            return this.typeName;
        }
    }
}
