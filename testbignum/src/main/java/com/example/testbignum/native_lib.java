package com.example.testbignum;

public class native_lib{
    static {
        System.loadLibrary("native_lib");
    }
    public static native String getRand();
    public static native String getAesEnc(String key,String content);
    public static native String getAesDec(String key,String content);
    public static native String testaes(String key,String content);
}
