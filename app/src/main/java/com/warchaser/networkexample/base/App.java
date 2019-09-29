package com.warchaser.networkexample.base;

import android.app.Application;

import com.warchaser.networkexample.util.Constants;
import com.warchaser.networklib.util.ErrorCodeUtil;

public class App extends Application {

    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        ErrorCodeUtil.initTokenCodes(
                Constants.TOKEN_EXPIRED,
                Constants.TOKEN_NEEDED,
                Constants.TOKEN_NOT_EXIST
        );
    }

    public static App getInstance(){
        return mInstance;
    }
}
