package com.example.socket_test.service_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.socket_test.MainActivity;
import com.example.socket_test.R;

//欢迎界面
public class SplashActivity extends Activity {

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

    //   判断进入主页面还是登录页面  -- 去服务器判断
    private void toMainOrLogin() {
        new Thread(){
            public void run(){
                //判断当前账号是否已登录过--进入好友页面或者登录页面
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                String id = "12345";
                intent.putExtra("id",id);
                startActivity(intent);
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        //发送两秒延时
        handler.sendMessageDelayed(Message.obtain(),2000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁消息
        handler.removeCallbacksAndMessages(null);
    }
}

