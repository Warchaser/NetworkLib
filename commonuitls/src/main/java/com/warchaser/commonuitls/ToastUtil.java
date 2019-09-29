package com.warchaser.commonuitls;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    private static Context APP_CONTEXT;

    private ToastUtil(){

    }

    public static void showToast(String message){
        Toast.makeText(getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(int resId){
        Toast.makeText(getAppContext(), resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(String message){
        Toast.makeText(getAppContext(), message, Toast.LENGTH_LONG).show();
    }

    public static void showToastLong(int resId){
        Toast.makeText(getAppContext(), resId, Toast.LENGTH_LONG).show();
    }

    private static Context getAppContext(){
        return APP_CONTEXT;
    }

    public static void initContext(Context context){
        APP_CONTEXT = context;
    }

}
