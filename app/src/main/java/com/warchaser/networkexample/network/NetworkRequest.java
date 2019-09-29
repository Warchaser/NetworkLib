package com.warchaser.networkexample.network;

import com.google.gson.JsonObject;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.warchaser.networkexample.util.Constants;
import com.warchaser.networklib.provider.NetWorkProvider;

import org.reactivestreams.Subscriber;

import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import static com.warchaser.networklib.util.RxJavaUtils.bindUntilEvent;
import static com.warchaser.networklib.util.RxJavaUtils.subscribe;

/**
 * 网络请求一层封装
 */
final public class NetworkRequest {

    private static NetworkRequest mInstance;
    private CommonRequest mCommonRequest;
    private OkHttpClient mOkHttpClient;
    private NetworkRequest() {
    }

    public static NetworkRequest getInstance() {
        if (mInstance == null) {
            synchronized (NetworkRequest.class) {
                if (mInstance == null) {
                    mInstance = new NetworkRequest();
                }
            }
        }
        return mInstance;
    }

    private synchronized Retrofit provideRetrofit() {
        if (mOkHttpClient == null) {
            mOkHttpClient = NetWorkProvider.getOkHttpClientWithInterceptors(new RequestInterceptor());
        }

        return NetWorkProvider.getRetrofit(mOkHttpClient, Constants.BASE_URL);
    }

    private synchronized CommonRequest getService() {
        if(mCommonRequest == null){
            mCommonRequest = provideRetrofit().create(CommonRequest.class);
        }
        return mCommonRequest;
    }

    public void getUsersByIndex(String pageNum, String pageSize, Subscriber<JsonObject> subscriber, LifecycleProvider lifecycleProvider){
        Flowable<JsonObject> flowable = getService().getUsersByIndex(pageNum, pageSize).compose(bindUntilEvent(lifecycleProvider));
        subscribe(flowable, subscriber);
    }

}
