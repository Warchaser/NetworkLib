package com.warchaser.networklib.upload;

import com.warchaser.commonuitls.PackageUtil;
import com.warchaser.networklib.util.NLog;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.lang.ref.WeakReference;

public class FileUploadSubscriber<T> implements Subscriber<T> {

    private final String TAG = "UPLOAD_SUBSCRIBE";
    private final String THIS = PackageUtil.getSimpleClassName(this);

    private WeakReference<UploadCallback> mCallBack;

    private Subscription mSubscription;

    public FileUploadSubscriber(UploadCallback callback){
        setUploadCallback(callback);
    }

    public void setUploadCallback(UploadCallback callback){
        this.mCallBack = new WeakReference<>(callback);
    }

    @Override
    public void onSubscribe(Subscription s) {
        if(mCallBack != null){
            mCallBack.get().onRequest();
        }

        mSubscription = s;
    }

    @Override
    public void onNext(T t) {
        NLog.i(TAG, THIS + "onNext()");
    }

    @Override
    public void onError(Throwable t) {
        if(mCallBack != null){
            mCallBack.get().onUploadFailed(t);
        }
    }

    @Override
    public void onComplete() {
        NLog.i(TAG, THIS + "onComplete()");
    }

    public void onUploadSuccess(){
        if(mCallBack != null){
            mCallBack.get().onUploadSuccess();
        }
    }

    public void onUploadProgress(long written, long contentLength){
        if(mCallBack != null){
            mCallBack.get().onProgress(written, contentLength);
        }
    }

    public void cancel(){
        onComplete();
        if(mSubscription != null){
            mSubscription.cancel();
            mSubscription = null;
        }
    }

}
