package com.warchaser.networklib.util;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Subscriber;

import java.lang.reflect.ParameterizedType;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public final class RxJavaUtils {

    private RxJavaUtils(){

    }

    /**
     * 统一的RxJava订阅处理
     */
    public static synchronized <T> void subscribe(Flowable<T> flowable, Subscriber<T> subscriber) {
        flowable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenNetworkError())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 统一的RxJava订阅处理
     */
    public static synchronized <T> void subscribe(Flowable<T> flowable, Subscriber<T> subscriber, Action doFinally) {
        flowable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenNetworkError())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(doFinally)
                .subscribe(subscriber);
    }

    /**
     * flatMap
     * */
    public static synchronized <T, R> void flatMap(Function<T, Flowable<R>> function, Flowable<T> flowable, Subscriber<R> subscriber){
        flowable.flatMap(function)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenNetworkError())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 获取Rx父类的LifeProvider的泛型类型，并返回要销毁时的Event类型
     * */
    public static synchronized Object getEvent(LifecycleProvider lifecycleProvider){
        Class<?> superclass = lifecycleProvider.getClass().getSuperclass();
        while (superclass != null) {
            if(superclass.getName().contains("Rx")){
                break;
            }
            superclass = superclass.getSuperclass();
        }

        Class eventClass = (Class<?>)((ParameterizedType)superclass.getGenericInterfaces()[0]).getActualTypeArguments()[0];

        Object event = ActivityEvent.DESTROY;
        if(eventClass == FragmentEvent.class){
            event = FragmentEvent.DESTROY;
        }

        return event;
    }

    public static synchronized <T>LifecycleTransformer<T> bindUntilEvent(LifecycleProvider<T> lifecycleProvider){
        return lifecycleProvider.bindUntilEvent((T) getEvent(lifecycleProvider));
    }

}
