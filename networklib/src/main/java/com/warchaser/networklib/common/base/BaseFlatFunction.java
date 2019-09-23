package com.warchaser.networklib.common.base;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public abstract class BaseFlatFunction<T, R> implements Function<T, Flowable<R>> {
    @Override
    public Flowable<R> apply(T t) throws Exception {
        return getNextAction(t);
    }

    public abstract Flowable<R> getNextAction(T t) ;
}
