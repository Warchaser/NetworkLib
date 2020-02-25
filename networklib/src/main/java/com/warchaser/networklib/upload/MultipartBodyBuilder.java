package com.warchaser.networklib.upload;

import com.warchaser.networklib.util.UUIDUtil;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MultipartBodyBuilder {

    public static MultipartBody file2MultipartBody(File file, String userId, String appId, RequestBody requestBody){
        final MultipartBody.Builder builder = new MultipartBody.Builder();

        builder.addFormDataPart("user_id", userId);
        builder.addFormDataPart("app_id", appId);
        builder.addFormDataPart("uuid", UUIDUtil.getUuidFileName(file.getName()));

        builder.addFormDataPart("db", file.getName(), requestBody);

        builder.setType(MultipartBody.FORM);

        return builder.build();

    }

}
