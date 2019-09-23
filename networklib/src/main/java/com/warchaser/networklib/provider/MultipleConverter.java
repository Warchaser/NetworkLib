package com.warchaser.networklib.provider;

import androidx.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

final public class MultipleConverter extends Converter.Factory {

    private final Converter.Factory mJsonFactory = GsonConverterFactory.create();

    public static MultipleConverter create() {
        return new MultipleConverter();
    }

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        for (Annotation annotation : methodAnnotations) {
            String value = ((ResponseFormat) annotation).value();
            if (ResponseFormat.JSON.equals(value)) {
                return mJsonFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        for (Annotation annotation : annotations) {

            String value = ((ResponseFormat) annotation).value();

            if (ResponseFormat.JSON.equals(value)) {
                return mJsonFactory.responseBodyConverter(type, annotations, retrofit);
            }
        }

        return null;
    }
}
