package com.warchaser.networklib.download;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.lang.ref.WeakReference;

/**
 * 下载用Subscriber
 * */
final public class SingleDownloadSubscriber<T> implements Subscriber<T> {
    private WeakReference<DownloadProgressCallBack> mFileCallBack;

    private Subscription mSubscription;

    public SingleDownloadSubscriber(){

    }

    public SingleDownloadSubscriber(DownloadProgressCallBack fileCallBack){
        setProgressCallBack(fileCallBack);
    }

    public SingleDownloadSubscriber setProgressCallBack(DownloadProgressCallBack fileCallBack){
        this.mFileCallBack = new WeakReference<>(fileCallBack);
        return this;
    }

    @Override
    public void onSubscribe(Subscription s) {
        if (mFileCallBack != null){
            mFileCallBack.get().onRequest();
        }

        mSubscription = s;
    }

    @Override
    public void onNext(T t) {
//        if (mFileCallBack != null){
//            mFileCallBack.get().onSuccess(t);
//        }
    }

    @Override
    public void onError(Throwable e) {
        if (mFileCallBack != null){
            mFileCallBack.get().onError(e);
        }
    }

    @Override
    public void onComplete() {
//        if (mFileCallBack != null){
//            mFileCallBack.get().onCompleted();
//        }
    }

    /**
     * 取消订阅
     * */
    public void cancel(){

        onComplete();

        if(mSubscription != null){
            mSubscription.cancel();
            mSubscription = null;
        }
    }

}
