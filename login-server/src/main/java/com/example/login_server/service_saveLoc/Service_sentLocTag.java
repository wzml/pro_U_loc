package com.example.login_server.service_saveLoc;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.login_server.MyUtils.SqliteMain;
import com.example.login_server.MyUtils.manageTxt;
import com.example.login_server.MyUtils.native_lib;
import com.example.login_server.UserInfo.UserActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Service_sentLocTag extends Service {
    public String tag = "F";
    protected String myName;
    private String id;
    public String pubkey;
    Double locj = 0.0;
    Double locw = 0.0;
    private static final long minTime = 2000;
    private static final float minDistance = 1;
    private LocationManager locationManager;
    Handler handler = new Handler();
    manageTxt txtHelper = new manageTxt();
    static {
        System.loadLibrary("native_lib");
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getTag();  //  获取当前位置tag
            Log.v("handler：",locj+"  "+locw);
            if(locj != 0.0 && locw != 0.0){
                Log.v("handler：","进入Thread");
                //  获取k1,k2
                SqliteMain sqliteMain = new SqliteMain(UserActivity.useractivity);
                SQLiteDatabase db  =sqliteMain.getReadableDatabase();
                sqliteMain.updateUserInfo(db,id,"tag",tag);
                db.close();
                System.out.println("tag:"+tag);
                //  SqliteMain sqliteMain = new SqliteMain(this);
                native_lib.getk1k2(tag,pubkey);
                final String k1 = txtHelper.getline(1);  //  第一行k1
                //System.out.println("k1:"+k1);
                final String k2 = txtHelper.getline(2);  //  第二行k2
                //System.out.println("k2:"+k2);
                final String ctr = txtHelper.getTime();  //  获取当前时间
                txtHelper.clearTxt();  //  清空文本
                new Thread(){
                    public void run(){
                        try{
                            System.out.println("Service_sent+runnable："+myName+id);
                            //  发送的用户昵称
                            sentLocTag adf = new sentLocTag();
                            adf.findserver(myName,k1,k2,ctr);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
            handler.postDelayed(this,1000*5); //5s上传一次tag
        }
    };

    //  获取当前所在位置标签,tag = 27：不在已知建筑物中
    public String getTag() {
        //  获取当前位置坐标
        if(locj!=0.0 && locw!=0.0){
            String lat=String.format("%.5f",locw);
            locw = Double.parseDouble(lat);
            String lon = String.format("%.5f",locj);
            locj = Double.parseDouble(lon);
        }else{
            return "F";
        }
        //  获取文本内容
        String readStr = "";
        readStr = getFromAssets(this,"GPSmessage.txt");

        //  获取当前位置标签
        if(readStr != null){
            JudgeBuildId judId = new JudgeBuildId();
            //System.out.println("location："+judId.JudMain(readStr,29.54561,108.14092));
            tag = String.valueOf(judId.JudMain(readStr,locw,locj));  //  tag = -1表示不在已知建筑物中
        }
        return tag;
    }

    //  读取assets文件夹中文本内容--输入文件名，输出文本内容/null
    public static String getFromAssets(Context context,String fileName){
        InputStreamReader inputReader = null;
        try{
            inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            StringBuilder result = new StringBuilder();
            String line;
            while((line = bufReader.readLine())!= null){
                result.append(line);
            }
            String var = result.toString();
            return var;
        }catch (Exception var){
            var.printStackTrace();
        }finally {
            if(inputReader != null){
                try{
                    inputReader.close();
                }catch (IOException var){
                    var.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public void onCreate(){   //  在开始服务时调用
        super.onCreate();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        handler.removeCallbacks(runnable); //停止计时器
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        id = intent.getStringExtra("id");
        getName();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            //return;
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    locj = location.getLongitude();
                    locw = location.getLatitude();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
//
//        if(prikey == 0){  //  最初初始化prikey为0，当获取过私钥后将不再重新获取
//            encryptTag encry = new encryptTag();
//            prikey = encry.RandomNumber();  //  获取私钥
//        }

        System.out.println("Serv_test+onBind："+myName+id);
        //throw new UnsupportedOperationException("Not yet implemented");
        return new idBinder();
    }

    //  获取用户昵称
    private void getName() {
                    SqliteMain sqliteMain = new SqliteMain(UserActivity.useractivity);
                    SQLiteDatabase db = sqliteMain.getReadableDatabase();
                    myName = sqliteMain.queryUserRecord(db,id,"name");
                    pubkey = sqliteMain.queryUserRecord(db,id,"pubkey");
                    db.close();
    }

    public class idBinder extends Binder {
        public void MyMethod(){
            handler.postDelayed(runnable,1000*3);//3s延迟
        }
    }
}