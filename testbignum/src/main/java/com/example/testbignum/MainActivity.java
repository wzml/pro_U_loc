package com.example.testbignum;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.testbignum.AEStest.AESUtilsz;
import com.example.testbignum.test.SqliteMain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

//  AppCompatActivity
public class MainActivity extends AppCompatActivity {
//    static {
//        System.loadLibrary("native_lib");
//    }
    TextView tv;
    private static Context mcontext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mcontext = getApplicationContext();
        //  添加内容进txt
        //getqx();
        //initDate();
        //  test:调用cpp文件
        tv = findViewById(R.id.tv);
        tv.setText("ada");
        AESUtilsz aesUtilsz = new AESUtilsz();
        try {
            tv.setText(aesUtilsz.encrypt("123456","1"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //cleartxt();
        //tv.setText(native_lib.getRand());
        //String tvtxt = native_lib.getAesEnc("1231","3123123123gj");
        //tvtxt += "\n" + native_lib.getAesDec("1231",tvtxt);
        //tv.setText(tvtxt);
        //SqliteMain sqliteMain = new SqliteMain(this);
        //sqliteMain.clearTable();
        //tv.setText(sqliteMain.getcpp());
        //tv.setText(getline(3));
        //Log.e("调用cpp:",sqliteMain.getcpp());

        //  网络权限管理
        //netmanage();
//        try{
//            if(NetworkUtils.iConnected(this) == false){
//                new AlertDialog.Builder(this)
//                        .setIcon(R.mipmap.ic_launcher)
//                        .setTitle("网络提醒")
//                        .setMessage("无网络连接！请先设置一种网络连接...")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                                //  进入网络连接设置
////                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
////                                //  有返回值的调用，设置网络请求码为2
////                                startActivityForResult(intent,2);
//                            }
//                        }).show();
//                Toast.makeText(this,"未连接网络！",Toast.LENGTH_SHORT).show();
//            }


//            final ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                cm.requestNetwork(new NetworkRequest.Builder().build(),new ConnectivityManager.NetworkCallback(){
//                    @Override
//                    public void onLost(Network network){
//                        super.onLost(network);
//                        //  网络不可用时
//                        Toast.makeText(MainActivity.this,"Wrong!",Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public void onAvailable(Network network){
//                        super.onAvailable(network);
//                        //  网络可用情况
//                        Toast.makeText(MainActivity.this,"is OK!",Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        Button bt = findViewById(R.id.testbig_main_bt);
        //  test：点击按钮后新增用户记录，并创建该好友表
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setSqlite(v); //  存入测试数据
                //SqliteMain sqliteMain = new SqliteMain(this);
                getSqlite(v);  //查询测试数据
            }
        });

    }

    private void netmanage() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        WifiManager wfm = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager cm =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(cm.getActiveNetwork() == null){  //  无网络连接时
                new AlertDialog.Builder(this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("网络提醒")
                        .setMessage("无网络连接！请先设置一种网络连接...")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //  进入网络连接设置
                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                //  有返回值的调用，设置网络请求码为2
                                startActivityForResult(intent,2);
                            }
                        }).show();
            }
        }
    }

    private void cleartxt() {
        String strFilePath = "/sdcard/NeedInfo/info.txt";
        try{
            File file = new File("/sdcard/NeedInfo/info.txt");
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
            e.printStackTrace();
        }
    }

    //  获取读写sd卡文本的权限
    private void getqx() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                } else{
                    ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},123);
                }
            }
            else{
                initDate();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[],int[] grantResults){
        switch (requestCode){
            case 123:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   initDate();  //  创建文本，并向其中写数据
                }else{

                }
                return;
            }
        }
    }

    //  路径，文件名
    private void initDate() {
        String filePath = "/sdcard/NeedInfo";
        String fileName = "info.txt";

        writeTxtToFile("txt nex",filePath,fileName);
    }

    //  获取指定行数据
    public String getline(int line){
        String result = null;
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream("/sdcard/NeedInfo/info.txt")
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

    //  将字符串写到文本文件中
    public void writeTxtToFile(String txt_content, String filePath, String fileName) {
        //  先生成文件夹，再生成文件
        makeFilePath(filePath,fileName);
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
            //  清空文本内容
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            //fileWriter.close();

            //  按行写入内容
            int i = 1;
            while(i <= 5){
                fileWriter.write("info" + i + "\n");
                i++;
            }
            fileWriter.flush();
            fileWriter.close();

//            RandomAccessFile raf = new RandomAccessFile(file,"rwd");
//            raf.seek(file.length());
//            raf.write(strContent.getBytes());
//            raf.close();

        }catch (Exception e){
            Log.e("TestFile","Error on write File:" + e);
        }
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

    public static Context getContext(){
        return mcontext;
    }
    //  测试输入数据
    public void setSqlite(View v){
        SqliteMain sqliteMain = new SqliteMain(this);
        //sqliteMain.setName("test0");
        sqliteMain.addUserRecord(1,"zll","123","24235436234234134324","33243543532433","4243425245","45345242434");
    }
    //  测试查询数据
    public void getSqlite(View v){
        SqliteMain sqliteMain = new SqliteMain(this);
        SQLiteDatabase sqLiteDatabase = sqliteMain.getReadableDatabase();
        String columns[] = {"name"};
        Cursor cursor = sqLiteDatabase.query("userinfo",columns,"id=123",null,null,null,null);
        String txt = null;
        if(cursor.moveToNext()){
            txt = cursor.getString(0);
        }
        tv.setText(txt);
        cursor.close();
        sqLiteDatabase.close();
    }
}

