package com.warchaser.networkexample.network;

import com.warchaser.commonuitls.AppManager;
import com.warchaser.networkexample.base.BaseActivity;
import com.warchaser.networklib.common.base.BaseSubscriber;

import org.reactivestreams.Subscription;

/**
 * 普通带Dialog的Subscriber
 * */
public class NormalSubscriber<T> extends BaseSubscriber<T> {

    private boolean mIsShowLoading = false;

    public NormalSubscriber(){

    }

    public NormalSubscriber(boolean isShowLoading){
        setIsShowLoading(isShowLoading);
    }

    @Override
    public final void onSubscribe(Subscription s) {
        super.onSubscribe(s);
        showLoading();
    }

    /**
     * 正常服务端返回数据后在此方法处理
     * */
    @Override
    public void onINext(T t){
        super.onINext(t);
    }

    @Override
    public final void onComplete() {
        super.onComplete();
        dismissLoading();
    }

    /**
     * Token过期
     * */
    @Override
    public final void onTokenExpire() {
        super.onTokenExpire();
        dismissLoading();
    }

    /**
     * Http Error
     * */
    @Override
    public void onError(Throwable t) {
        super.onError(t);
        dismissLoading();
    }

    /**
     * Token不存在(请求时为空串或Null或者此Token不存在于服务器)
     * */
    @Override
    public void onTokenNotExist() {
        super.onTokenNotExist();
        dismissLoading();
    }

    /**
     * 是否显示加载Loading Dialog
     * */
    public void setIsShowLoading(boolean isShowLoading){
        this.mIsShowLoading = isShowLoading;
    }

    /**
     * 请求结束后需要Dismiss Dialog
     * 可重写此方法处理此时的逻辑
     * */
    protected void dismissLoading(){
        BaseActivity activity = AppManager.getInstance().getLastActivity(BaseActivity.class);
        if(activity != null){
            activity.dismissLoadingDialog();
        }
    }

    /**
     * 请求开始, 需要Show Dialog
     * 可重写此方法处理此时的逻辑
     * */
    protected void showLoading(){
        if(mIsShowLoading){
            BaseActivity activity = AppManager.getInstance().getLastActivity(BaseActivity.class);
            if(activity != null){
                activity.showLoadingDialog();
            }
        }
    }
}
