package com.example.login_server.loginInfo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AlertDialog;

import com.example.login_server.MyUtils.NetworkUtils;
import com.example.login_server.MyUtils.SqliteMain;
import com.example.login_server.R;
import com.example.login_server.UserInfo.UserActivity;
import com.example.login_server.netty.nettyChannelFuture;
import com.example.login_server.netty.nettyTcpThread;

//欢迎界面
public class SplashActivity extends Activity {
    static {
        System.loadLibrary("native_lib");
    }
    public static SplashActivity splashActivity;
    private nettyTcpThread client;
    private boolean closeNetty = true;

    public nettyTcpThread getClient(){
        return client;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            //  如果当前activity已经退出，那么就不处理Handler中消息
            if(isFinishing()){
                return;
            }
            //  判断进入主页面还是登录页面
            toMainOrLogin();
        }
    };

    //   判断进入主页面还是登录页面  -- 本地数据库进行判断
    private void toMainOrLogin() {
        //判断当前账号是否已登录过--进入好友页面或者登录页面
        testCode();  //  测试所用代码--清空数据库中信息
        SqliteMain sqliteMain = new SqliteMain(SplashActivity.this);
        SQLiteDatabase db = sqliteMain.getReadableDatabase();
        String id;
        id = sqliteMain.queryOnlineId(db);
        db.close();
       // boolean a;
       // a = sqliteMain.queryExistId("12355");
       // a = sqliteMain.queryExistId("12356");

        if(id == null){  //  没有已知用户在线，进入登录页面
            closeNetty = false;
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else {  //  有已知用户登录，进入用户页面
            closeNetty = false;
            Intent intent = new Intent(SplashActivity.this, UserActivity.class);
            intent.putExtra("id",id);  //向下一个页面传参
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //  进入UserActivity函数空间后如果点击退出将不会回到flash.activity,因为清除了进程空间所有activity
            startActivity(intent);
        }

    }

    //  测试所用代码--清空数据库Userinfo表中所有数据，删除所有好友表项信息
    private void testCode() {
        //String buff = native_lib.stringFromJNI();
        //Toast.makeText(this,buff,Toast.LENGTH_SHORT).show();

        SqliteMain sqliteMain = new SqliteMain(SplashActivity.this);
        SQLiteDatabase db = sqliteMain.getReadableDatabase();
        sqliteMain.clearTable(db);
        //sqliteMain.addUserRecord(db,"12355","xa","1","N","312113","31231321");
        //String name = sqliteMain.queryUserRecord(db,"12355","name");
        db.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashActivity = this;
        client = new nettyTcpThread();

        //  查询网络状态，若未连接点击确定后将退出app
        NetworkUtils networkUtils = new NetworkUtils();
        if(networkUtils.iConnected(this) == false){
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.frader)
                    .setTitle("网络提醒")
                    .setMessage("网络连接不可用，请稍后重试...")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            //  进入网络连接设置
//                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
//                                //  有返回值的调用，设置网络请求码为2
//                                startActivityForResult(intent,2);
                        }
                    }).show();
        }else{
            //  连接至netty
            new Thread(){
                @Override
                public void run() {
                    try{
                        client.startConnect();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();

            //发送两秒延时
            handler.sendMessageDelayed(Message.obtain(),2000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁消息
        if (closeNetty){
            client.disconnect(nettyChannelFuture.getChannel());
        }
        handler.removeCallbacksAndMessages(null);
    }
}