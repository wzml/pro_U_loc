package com.example.login_server.MyUtils.log;

import android.util.Log;

//  打印并存储日志记录
public class LogUtils {

    public static void i(String title, String msg) {
        String str = formatString(title, msg);
        if (LogVariateUtils.getInstance().getIsShowLog())  //  是否在logcast中显示日志
            Log.i(LogVariateUtils.getInstance().getTag(), str);
        if (LogVariateUtils.getInstance().getIsWriteLog())  //  是否记录str在文本中
            LogFileUtils.writeLogFile(str);
    }

    public static void w(String title, String msg) {
        String str = formatString(title, msg);
        if (LogVariateUtils.getInstance().getIsShowLog())
            Log.w(LogVariateUtils.getInstance().getTag(), str);
        if (LogVariateUtils.getInstance().getIsWriteLog())
            LogFileUtils.writeLogFile(str);
    }

    public static void e(String title, String msg) {
        String str = formatString(title, msg);
        if (LogVariateUtils.getInstance().getIsShowLog())
            Log.e(LogVariateUtils.getInstance().getTag(), str);
        if (LogVariateUtils.getInstance().getIsWriteLog())
            LogFileUtils.writeLogFile(str);
    }

    public static String formatString(String title, String msg) {
        if (title == null) {
            return msg == null ? "" : msg;
        }
        return String.format("[%s]: %s", title, msg == null ? "" : msg);
    }
}