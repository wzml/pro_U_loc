package com.example.login_server.MyUtils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

public class manageTxt {
    //  路径，文件名
    String filePath = "/sdcard/NeedInfo";
    String fileName = "info.txt";

    //  filepath下生成名为fileName的txt文本---  创建成功返回true
    public boolean generateTxt(){
        if(makeFilePath(filePath,fileName) != null)
            return true;
        return false;
    }

    //  判断该文件是否存在，存在返回true,不存在返回false
    public boolean isexit(){
        File file = null;
        file = new File(filePath+"/"+fileName);
        if(!file.exists()){
            return false;
        }else {
            return true;
        }
    }

    //  清空txt文件
    public void clearTxt(){
        String strFilePath = filePath+"/"+fileName;
        try{
            File file = new File(strFilePath);
            if(!file.exists()){
                Log.d("TestFile","Create the file:"+strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            //  清空文本内容
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        }catch (Exception e){
            Log.e("TestFile","Error on write File:" + e);
        }
    }

    //  字符串按行追加至txt文件后
    public void writeToFile(String txt_content) {
        String strFilePath = filePath+"/"+fileName;
        //  每次写入时，都换行写
        String strContent = txt_content + "\n" ;
        try{
            File file = new File(strFilePath);
            if(!file.exists()){
                Log.d("TestFile","Create the file:"+strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file,"rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        }catch (Exception e){
            Log.e("TestFile","Error on write File:" + e);
        }
    }

    //  获取第line行的txt文本
    public String getline(int line){
        String result = null;
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(filePath+"/"+fileName)
                    )
            );
            int i = 1;
            String linestr;
            while(i != line && (linestr = br.readLine()) != null ){
                i++;
            }
            if((result = br.readLine()) != null){
                return result;
            };
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    //  生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try{
            file = new File(filePath+"/"+fileName);
            if(!file.exists()){
                file.createNewFile();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return file;
    }

    //  生成文件夹
    public void makeRootDirectory(String filePath) {
        File file = null;
        try{
            file = new File(filePath);
            if(!file.exists()){
                file.mkdir();
            }
        }catch (Exception e){
            Log.i("error:" ,e + "");
        }
    }

    //  获取当前的时间（min为单位）
    public String getTime() {
        long totalSeconds = System.currentTimeMillis()/1000;  //  获取自UNIX时间戳到现在（格林尼治时间）的总毫秒数，即是1970/1/1 0点到现在经过的毫秒数
        long currentMinutes = totalSeconds / 60;  //  获取当前分钟数
        return String.valueOf(currentMinutes);
    }

}
