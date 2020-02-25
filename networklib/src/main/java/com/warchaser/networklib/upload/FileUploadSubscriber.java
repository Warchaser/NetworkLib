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
    private final int REQUEST_COUNT = 1;

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
        //由于是Flowable包装的，所以需要Request
        s.request(REQUEST_COUNT);
    }

    @Override
    public void onNext(T t) {
        NLog.e(TAG, THIS + "onNext()");
        //下游Request,才会触发Next
        mSubscription.request(REQUEST_COUNT);
        if(t instanceof BaseUploadResp){
            if(((BaseUploadResp) t).getStatusCode() == 200){
                onUploadSuccess(t);
            } else {
                onUploadFailed(t);
            }
        }
    }

    @Override
    public void onError(Throwable t) {
        NLog.e(TAG, THIS + "onError()");
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

    public void onUploadFailed(T t){
        if(mCallBack != null){
            mCallBack.get().onUploadFailed(t);
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
