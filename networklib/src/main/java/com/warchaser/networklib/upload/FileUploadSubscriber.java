package com.warchaser.networklib.upload;

import com.warchaser.commonuitls.PackageUtil;
import com.warchaser.networklib.util.NLog;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.lang.ref.WeakReference;

public class FileUploadSubscriber<T> implements Subscriber<T> {

    private final String TAG = "UPLOAD_SUBSCRIBE";
    private final String THIS = PackageUtil.getSimpleClassName(this);

    private WeakReference<UploadCallback<T>> mCallBack;

    private Subscription mSubscription;

    public FileUploadSubscriber(UploadCallback<T> callback){
        setUploadCallback(callback);
    }

    public void setUploadCallback(UploadCallback<T> callback){
        this.mCallBack = new WeakReference<>(callback);
    }

    @Override
    public void onSubscribe(Subscription s) {
        NLog.e(TAG, THIS + "onSubscribe()");
        if(mCallBack != null){
            mCallBack.get().onRequest();
        }

        mSubscription = s;
    }

    @Override
    public void onNext(T t) {
        NLog.e(TAG, THIS + "onNext()");
        if(mCallBack != null){
            if(t instanceof BaseUploadResp){
                if(((BaseUploadResp) t).getStatusCode() == 200){
                    mCallBack.get().onUploadSuccess(t);
                }
            }
        }
    }

    @Override
    public void onError(Throwable t) {
        if(mCallBack != null){
            mCallBack.get().onUploadFailed(t);
        }
    }

    @Override
    public void onComplete() {
        NLog.e(TAG, THIS + "onComplete()");
    }

    public void onUploadSuccess(T t){
        if(mCallBack != null){
            mCallBack.get().onUploadSuccess(t);
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
