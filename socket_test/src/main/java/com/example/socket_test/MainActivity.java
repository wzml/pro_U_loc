package com.example.socket_test;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.socket_test.service_test.Service_test;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    TextView tv_GPS;
    Intent intent;
    LocationManager lm ;
    private String id;
    ServiceConnection conn = null;
    final static int LOCATION_SETTING_REQUEST_CODE = 100;
    private ListView lvfri;
    private List<Fri> friList = new ArrayList<Fri>();  //创建集合保存好友信息

    private Timer timer;
    private Handler mHandler = new Handler(){
      @Override
      public void handleMessage(Message msg){
          if(msg.what == 0){
              //充复执行的代码
              Toast.makeText(MainActivity.this,"演示",Toast.LENGTH_LONG).show();
          }
      }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_GPS = findViewById(R.id.tv_loc);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        System.out.println("MainAc+Create:"+id);
        lvfri = (ListView)findViewById(R.id.list_fri); //获得子布局
        getData();
        FriAdaopter friAdaopter = new FriAdaopter(this,R.layout.listview_item,friList); //关联数据和子布局
        lvfri.setAdapter(friAdaopter);  //绑定数据和适配器

        lvfri.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Fri fri = friList.get(position);
                Toast.makeText(MainActivity.this,fri.getName(),Toast.LENGTH_LONG).show();
            }
        });
        initview();

        //  动态申请危险权限
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //onRequestPermissionsResult();  为了请求权限，为了让权限授予时立即生效，一般需要重写权限回调方法
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            prepareLocatingService();
        }
        serviceStart();
    }

    //  准备获取位置信息
    private void prepareLocatingService() {
        // || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("消息框")
                .setMessage("请先打开定位服务")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //定位服务设置意图
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS
                        );
                        startActivityForResult(intent,LOCATION_SETTING_REQUEST_CODE);
                    }
                }).show();
    }

    //  已开启位置服务时
//    private void locatingService() {
//        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            //tv_GPS.setText("GPS定位中...");
//            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
//                return;
//            }
//            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
//                private Location location;
//                Intent intent = new Intent();
//                @Override
//                public void onLocationChanged(Location location) {
//                    locj = location.getLongitude();
//                    locw = location.getLatitude();
//                    //Log.v("MainLoc：",locj+"  "+locw);
//                    loc = "纬度为："+location.getLatitude()+"\n经度为："+location.getLongitude();
//                    tv_GPS.setText(loc);
//
//                    //发送广播
//                    intent.putExtra("locj",locj);
//                    intent.putExtra("locw",locw);
//                    intent.setAction("loc");
//                    sendBroadcast(intent);
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

    @Override  //有返回值调用的回调
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == LOCATION_SETTING_REQUEST_CODE){
            prepareLocatingService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode){
            case 1:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    prepareLocatingService();
                }else{
                    Toast.makeText(this,"没有授予定位权限",Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void serviceStart() {
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //  获取代理对象
                Service_test.idBinder idBinder = (Service_test.idBinder) service;
                //  调用代理方法
                idBinder.MyMethod();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //  断开服务连接
            }
        };
        Intent intent1 = new Intent(getApplicationContext(), Service_test.class); // 显示调用服务意图
        intent1.putExtra("id",id);
        System.out.println("MainAc+serStart："+id);
        intent1.setPackage("com.example.socket_test.service_test");
        bindService(intent1,conn,BIND_AUTO_CREATE);
        //startService(intent);  //  开启服务
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(intent!=null) {
            unbindService(conn);  // 解除绑定
            stopService(intent);  // 结束服务
        }

        finish();
    }

    private void initview() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 0;
                mHandler.sendMessage(message);
            }
        },1000,60*1000);
    }

    private void getData() {
        int[] imageIds = {
                R.drawable.show_fri,R.drawable.show_fri,
                R.drawable.show_fri,R.drawable.show_fri,
                R.drawable.show_fri,R.drawable.show_fri,
                R.drawable.show_fri,R.drawable.show_fri
        };
        String[] names = {"alice","bob",
                "tom","min",
                "max","hias",
                "dad","mommy"
        };
        for(int i = 0;i<imageIds.length;i++){  //将数据添加到集合中
            friList.add(new Fri(imageIds[i],names[i]));  //将图片id和对应name存储到一起
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

}