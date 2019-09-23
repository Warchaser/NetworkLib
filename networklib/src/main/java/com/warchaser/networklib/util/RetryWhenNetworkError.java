package com.warchaser.networklib.util;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

/**
 * 网络出错重试
 * */
public class RetryWhenNetworkError implements Function<Flowable<? extends Throwable>, Flowable<?>> {

    /**
     * 重试次数
     * */
    private int mRetryCount = 3;

    /**
     * 在N秒后再次重试延迟
     * */
    private long mRetryDelay = 3_000;

    /**
     * 在N秒后再次重试的延迟叠加时间
     * */
    private long mRetryDelayAccumulation = 3_000;

    public RetryWhenNetworkError(){

    }

    public RetryWhenNetworkError(int retryCount, long retryDelay, long retryDelayAccumulation){
        mRetryCount = retryCount;
        mRetryDelay = retryDelay;
        mRetryDelayAccumulation = retryDelayAccumulation;
    }

    @Override
    public Flowable<?> apply(Flowable<? extends Throwable> flowable) {
        return flowable.zipWith(Flowable.range(1, mRetryCount + 1), new BiFunction<Throwable, Integer, Wrapper>() {
            @Override
            public Wrapper apply(Throwable throwable, Integer integer) throws Exception {
                return new Wrapper(integer, throwable);
            }
        }).flatMap(new Function<Wrapper, Flowable<?>>() {
            @Override
            public Flowable<?> apply(Wrapper wrapper) throws Exception {
                if(isNeedRetry(wrapper)){
                    return Flowable.timer(mRetryDelay + (wrapper.getIndex() - 1) * mRetryDelayAccumulation, TimeUnit.MILLISECONDS);
                }

                return Flowable.error(wrapper.getThrowable());
            }
        });
    }

    private boolean isNeedRetry(Wrapper wrapper){
        return (wrapper.getThrowable() instanceof ConnectException
                || wrapper.getThrowable() instanceof SocketTimeoutException
                || wrapper.getThrowable() instanceof TimeoutException)
                && wrapper.getIndex() < mRetryCount + 1;
    }

    private class Wrapper{
        private int mIndex;
        private Throwable mThrowable;

        Wrapper(int index, Throwable throwable){
            mIndex = index;
            mThrowable = throwable;
        }

        int getIndex(){
            return mIndex;
        }

        Throwable getThrowable(){
            return mThrowable;
        }
    }
}
