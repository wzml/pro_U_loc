package com.example.login_server.MyUtils;

public class native_lib {
    static {
        System.loadLibrary("native_lib");
    }
    public static native String stringFromJNI();
    public static native String getHX();
    public static native String getk1k2(String tag,String h);
    public static native String judClose();
}
