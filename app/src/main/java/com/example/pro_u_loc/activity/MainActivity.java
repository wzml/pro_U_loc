package com.example.pro_u_loc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pro_u_loc.AES.AESUtilsz;
import com.example.pro_u_loc.utils.DBUtils;
import com.example.pro_u_loc.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.what == 1) {  //用户存在，进入用户页面
                judgeinfo(msg.obj.toString()); //跳转页面
            }else if (msg.what == 2){
                signinfo(msg.obj.toString());  //跳转页面
             } else {    //Toast必须在线程中
                Toast.makeText(MainActivity.this, "账号或密码错误！", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

//    Handler handler1 = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(@NonNull Message msg) {
//             if (msg.what == 2){
//                signinfo(msg.obj.toString());  //跳转页面
//             } else {
//                 Toast.makeText(MainActivity.this, "账号申请失败！", Toast.LENGTH_SHORT).show();
//             }
//            return false;
//        }
//    });

    static{
        System.loadLibrary("native-lib");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //控件声明
        final EditText id = findViewById(R.id.id_show);  //不加final没办法进入onclick中
        final EditText password = findViewById(R.id.password);
        TextView txt = findViewById(R.id.textView);
        Button login = findViewById(R.id.login);
        Button sign = findViewById(R.id.sign);
        AESUtilsz aesUtilsz = new AESUtilsz();
        String data = "102009862644640823556228816978959977369968650415611247890918102926379260326023414536954187279361835941277010271182216927649680652608774960480147639492";
        String str = "源数据：，加密后：";
        String str1= null;
        try {
            str1 = aesUtilsz.encrypt(data,"1");
            str += str1;
            str += "解密后：";
            str += aesUtilsz.decrypt(str1,"1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        txt.setText(str);

        login.setOnClickListener(new View.OnClickListener() {  //登录
            @Override
            public void onClick(View v) {
                final String id1 = id.getText().toString().trim();  //.trim删除字符串头尾空白符
                final String password1 = password.getText().toString().trim();
                Log.e(TAG,id1);
                if(id1 == null || id1.equals("")) {  //账号不为空
                    Toast.makeText(MainActivity.this,"账号不能为空",Toast.LENGTH_SHORT).show();
                }
                else if(password1 == null || password1.equals("")) {  //密码不为空
                    Toast.makeText(MainActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String name = DBUtils.judgeUserinfo(1,id1,password1);
                            Message msg = new Message();
                            if(name == null) {
                                msg.what = 0;
                                msg.obj = "账户或密码错误！";
                            } else {
                                msg.what = 1;
                                msg.obj = name;
                            }
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {  //注册
            String idx = null;
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        idx = DBUtils.judgeUserinfo(2,null,null);   //申请一个账号
                        Message msg1 = new Message();
                        msg1.what = 2;
                        msg1.obj = idx;
                        handler.sendMessage(msg1);
                    }
                }).start();
            }
        });

    }

    public void judgeinfo(String name) {  //进入用户界面
            Intent intent = new Intent(this, activity_user.class);
            intent.putExtra("username",name);  //向下一个页面传参
            startActivity(intent);
            // finish();
    }

    public void signinfo(String id) {  //进入注册页面
            Intent intent = new Intent(this,signActivity.class);
            intent.putExtra("id",id);  //向下一个页面传参
            startActivity(intent);
    }

}
