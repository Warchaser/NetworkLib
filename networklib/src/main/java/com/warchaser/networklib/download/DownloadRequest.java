package com.warchaser.networklib.download;

import com.warchaser.networklib.provider.NetWorkProvider;
import com.warchaser.networklib.util.RetryWhenNetworkError;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * 下载请求入口管理类
 * */
public final class DownloadRequest {

    private volatile static DownloadRequest mInstance;

    private DownloadRetrofitService mService;

    private final String KEY_APK_DOWNLOAD = "APK_DOWNLOAD";

    private DownloadRequest(){

    }

    public static DownloadRequest getInstance(){
        if(mInstance == null){
            synchronized (DownloadRequest.class){
                if(mInstance == null){
                    mInstance = new DownloadRequest();
                }
            }
        }

        return mInstance;
    }

    private synchronized DownloadRetrofitService getService(){
        if(mService == null){
            mService = NetWorkProvider.getDownloadRetrofit("").create(DownloadRetrofitService.class);
        }
        return mService;
    }

    /**
     * 下载文件
     */
    public void downloadFile(String url, DownloadProgressCallBack<ResponseBody> callBack, SingleDownloadSubscriber subscriber){
        Flowable<ResponseBody> flowable = getService().downloadFile(url);
        downloadSubscribe(flowable, callBack, subscriber);
    }

    /**
     * 订阅下载
     */
    private synchronized <T> void downloadSubscribe(Flowable<T> flowable, final DownloadProgressCallBack<ResponseBody> callBack, SingleDownloadSubscriber subscriber) {

        final Function<ResponseBody, ResponseBody> function = new Function<ResponseBody, ResponseBody>() {
            @Override
            public ResponseBody apply(ResponseBody responseBody) throws Exception {
                if (callBack != null) {
                    callBack.saveFile(responseBody);
                }
                return responseBody;
            }
        };

        flowable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenNetworkError())
                .map((Function<? super T, ?>) function)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
