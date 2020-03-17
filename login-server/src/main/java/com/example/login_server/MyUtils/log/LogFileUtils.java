package com.example.login_server.MyUtils.log;

import com.example.login_server.MyUtils.GPSGetNewLoc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//  存储日志

public class LogFileUtils {

    private static Object obj = new Object();

    //文件名称
    private static String fileName = "FRader-" + "Log日志" + ".log";

    //文件路径
    private static String filePath ="/sdcard/Log日志/";  //   Environment.getExternalStorageDirectory().getAbsolutePath()

    //   将mag写入文件
    public static void writeLogFile(String msg) {
        synchronized (obj) {  //  synchronized：同步锁
            try {
                createFile();
                File file = new File(filePath + fileName);
                FileWriter fw = null;
                if (file.exists()) {
                    if (file.length() > LogVariateUtils.getInstance().getFileSize())
                        fw = new FileWriter(file, false);  //  不在原有基础上继续写
                    else
                        fw = new FileWriter(file, true);  //  在原有基础上继续写
                } else{
                    fw = new FileWriter(file, false);
                }

                Date d = new Date();
                SimpleDateFormat s = new SimpleDateFormat("MM-dd HH:mm");
                String dateStr = s.format(d);
                String dataLoc = GPSGetNewLoc.longitude + "," + GPSGetNewLoc.latitude;

                //  写入格式，[03-21 12:32]  Loc：123.4231,125.34231
                fw.write(String.format("[%s]  Loc：[%s]  %s  ", dateStr,dataLoc,msg));
                fw.write(13);  //   写入\r
                fw.write(10);  //  写入\n  在windows系统中换行需要\r\n，在Unix内核系统中换行就是\n
                fw.flush();
                fw.close();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    //  读文件  --返回文件内容
    public static String readLogText() {
        FileReader fr = null;
        try {
            File file = new File(filePath + fileName);
            if (!file.exists()) {
                return "";
            }
            long n = LogVariateUtils.getInstance().getFileSize();
            long len = file.length();
            long skip = len - n;
            fr = new FileReader(file);
            fr.skip(Math.max(0, skip));  //  跳过长度Math.max(0,skip)
            char[] cs = new char[(int) Math.min(len, n)];  //  读取最新的内容，长度为min(len,n)
            fr.read(cs);
            return new String(cs).trim();  //  .trim截取字符串中间的非空白字符
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (fr != null)
                    fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    //  创建文件夹
    public static void createFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
    }
}