package com.warchaser.networklib.util;

public class ErrorCodeUtil {

    //Token失效
    public static int TOKEN_EXPIRED;

    //需要token
    public static int TOKEN_NEEDED;

    //Token不存在
    public static int TOKEN_NOT_EXIST;

    public static void initTokenCodes(
            final int tokenExpired,
            final int tokenNeeded,
            final int tokenNotExist){
        TOKEN_EXPIRED = tokenExpired;
        TOKEN_NEEDED = tokenNeeded;
        TOKEN_NOT_EXIST = tokenNotExist;
    }


}
