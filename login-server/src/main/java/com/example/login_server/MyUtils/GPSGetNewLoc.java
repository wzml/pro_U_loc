package com.example.login_server.MyUtils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.login_server.UserInfo.UserActivity;

public class GPSGetNewLoc {
    private String TAG = "MyListener";
    private LocationManager lm;
    private ConnectivityManager cm;
    private WifiManager wfm;

    public static String longitude;
    public static String latitude;

    public void prepareLocationService() {
        lm = (LocationManager) UserActivity.userActivity.getSystemService(Context.LOCATION_SERVICE);
        wfm = (WifiManager) UserActivity.userActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        cm = (ConnectivityManager)UserActivity.userActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||  lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locatingService();
        }else{
            Log.i(TAG,"未打开位置服务");
        }
    }

    private void locatingService() {
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ){
            NetWorkLocation();
        } else if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            GpsLocation();
        }
    }

    private void GpsLocation() {
        if (ActivityCompat.checkSelfPermission((UserActivity.userContext),
                Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            Log.i(TAG,"GPS权限未打开");
        }else{
            Log.i(TAG,"采用GPS获取位置信息");
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }

    private void NetWorkLocation() {
        if (cm.getActiveNetworkInfo() != null){
            if(wfm.isWifiEnabled()){
                Log.i(TAG,"采用wifi方式定位");
            }else {
                Log.i(TAG,"采用基站方式定位");
            }
        }else {
            Log.i(TAG,"无网络连接");
        }
        if (ActivityCompat.checkSelfPermission(UserActivity.userContext,Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "未开始网络权限");
            return;
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1,locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitude = String.valueOf(location.getLongitude());
            latitude = String.valueOf(location.getLatitude());
            Log.i(TAG,"--------------经度为："+longitude+" 纬度为："+latitude);
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
    };

    //  停止GPS定位功能
    public void stop(){
        if (UserActivity.userContext != null) {
            if (ActivityCompat.checkSelfPermission(UserActivity.userContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UserActivity.userContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            lm.removeUpdates(locationListener);
            lm = null;
            if (locationListener != null)
                locationListener = null;
        }
    }
}