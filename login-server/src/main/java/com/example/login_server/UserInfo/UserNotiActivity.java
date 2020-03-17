package com.example.login_server.UserInfo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login_server.MyUtils.log.LogFileUtils;
import com.example.login_server.R;

public class UserNotiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_noti);

        TextView showtxt = findViewById(R.id.user_noti_tv);
        Intent intent = getIntent();
        String cRes = intent.getStringExtra("cRes");
        //System.out.println("---------"+cRes);
        if (cRes != null && !cRes.equals("log")){
            showtxt.setText(cRes);
        }else if(cRes.equals("log")){  //  查看日志
            String text = LogFileUtils.readLogText();
            System.out.println("-------------"+text);
            showtxt.setText(LogFileUtils.readLogText());
        }else{
            showtxt.setText("等待获取当前位置...");
        }
    }
}
