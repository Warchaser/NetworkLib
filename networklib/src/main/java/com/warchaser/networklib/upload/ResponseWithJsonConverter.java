package com.warchaser.networklib.upload;

import androidx.annotation.Nullable;

import com.warchaser.networklib.provider.MultipleConverter;
import com.warchaser.networklib.provider.ResponseFormat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResponseWithJsonConverter extends Converter.Factory{

    private final Converter.Factory mJsonFactory = GsonConverterFactory.create();

    public static MultipleConverter create() {
        return new MultipleConverter();
    }

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
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
