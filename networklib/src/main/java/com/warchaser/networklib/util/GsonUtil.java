package com.warchaser.networklib.util;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Gson
 * 解析工具静态类
 * */
final public class GsonUtil {

    public synchronized static <T> ArrayList<T> parseString2List(String json, Class clazz) {
        Type type = new ParameterizedTypeImpl(clazz);
        ArrayList<T> list = new Gson().fromJson(json, type);
        return list;
    }

    public synchronized static <T>T parseString2Object(String json, Class<T> clazz){
        return new Gson().fromJson(json, clazz);
    }

    private static class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }
        @Override
        public Type getRawType() {
            return List.class;
        }
        @Override
        public Type getOwnerType() {
            return null;
        }
    }

}
