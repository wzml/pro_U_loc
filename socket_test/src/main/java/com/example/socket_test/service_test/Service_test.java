package com.example.socket_test.service_test;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Service_test extends Service {
    private static final long minTime = 2000;
    private static final float minDistance = 1;
    private LocationManager locationManager;
    protected String tag = "1";
    protected String id;
    //protected  myReceiver myreceiver;
    Double locj = 0.0;
    Double locw  = 0.0;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //获取当前位置tag
            getTag();
            Log.v("handler：",locj+"  "+locw);
            if(locj != 0.0 && locw != 0.0){
                Log.v("handler：","进入Thread");
                new Thread(){
                public void run(){
                    try{
                        addFriendUtils adf = new addFriendUtils();
                        adf.findserver(id,tag);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }.start();
            }
           handler.postDelayed(this,1000*5); //5s上传一次tag
        }
    };

    //  已开启位置服务时
//    public void locatingService() {
//        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            //tv_GPS.setText("GPS定位中...");
//            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
//                return;
//            }
//            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
//               // private Location location;
//                @Override
//                public void onLocationChanged(Location location) {
//                    locj = location.getLongitude();
//                    locw = location.getLatitude();
//                }
//
//                @Override
//                public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                }
//
//                @Override
//                public void onProviderEnabled(String provider) {
//
//                }
//
//                @Override
//                public void onProviderDisabled(String provider) {
//
//                }
//
//            });
//        }
//    }

    //  获取当前所在位置标签,tag = -1：不在已知建筑物中
    private void getTag() {
        //  获取当前位置坐标
        if(locj!=0 && locw!=0){

            String lat=String.format("%.5f",locw);
            locw = Double.parseDouble(lat);
            String lon = String.format("%.5f",locj);
            locj = Double.parseDouble(lon);
        }
        System.out.println("Ser_test+getTag1："+id);
        //  获取文本内容
        String readStr = "";
        readStr = getFromAssets(this,"GPSmessage.txt");

        //  获取当前位置标签
        if(readStr != null){
            JudgeBuildId judId = new JudgeBuildId();
            tag = String.valueOf(judId.JudMain(readStr,locw,locj));  //  tag = -1表示不在已知建筑物中
        }

    }


    //读取assets文件夹中文本内容--输入文件名，输出文本内容/null
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
        //unregisterReceiver(myreceiver);
        handler.removeCallbacks(runnable); //停止计时器
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        id = intent.getStringExtra("id");
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
        return new idBinder();
    }

    public class idBinder extends Binder{
        public void MyMethod(){
            handler.postDelayed(runnable,1000*3);
        }
    }

}