package com.example.login_server.loginInfo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login_server.MyUtils.AESUtilsz;
import com.example.login_server.MyUtils.MD5Encode;
import com.example.login_server.MyUtils.SqliteMain;
import com.example.login_server.MyUtils.manageTxt;
import com.example.login_server.MyUtils.native_lib;
import com.example.login_server.R;
import com.example.login_server.UserInfo.addFriendUtils;
import com.example.login_server.netty.*;

import java.io.IOException;

public class SignActivity extends AppCompatActivity {
    public static SignActivity signtivity;
    public String pubkey,seckey,toserverSeckey;
    public String name1,password1,toserverPass;
    private boolean closeNetty = true;
    private nettyTcpThread client;


    static {
        System.loadLibrary("native_lib");
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Toast.makeText(SignActivity.this,"已存在该用户！",Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    savetoServer();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        signtivity = this;
        client = new nettyTcpThread();

        Button sign = findViewById(R.id.zc_bt_sign);
        final EditText name = findViewById(R.id.zc_et_name);
        final EditText password = findViewById(R.id.zc_et_password);

        //  注册
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name1 = name.getText().toString();
                password1 = password.getText().toString();
                manageTxt txtHelper = new manageTxt();
                if(txtHelper.isexit() == false){  //  不存在txt文本的话 创建txt文本
                    if(false == txtHelper.generateTxt())  //  创建失败则无法注册
                        return;
                }
                txtHelper.clearTxt();  //  清空原有数据信息。
                if(name1 == null || name1.equals("") || name1.equals("F"))
                    Toast.makeText(SignActivity.this,"昵称错误！",Toast.LENGTH_LONG).show();
                else if(password1 == null || password1.equals("")){
                    Toast.makeText(SignActivity.this,"密码错误！",Toast.LENGTH_LONG).show();
                    //show1dialog("31");
                }
                else {
                    judgename(name1);
                }
            }
        });

    }


    private void savetoServer() {
        manageTxt txtHelper = new manageTxt();
        //  获取私钥与公钥
        //native_lib.getHX(); //  将h,x写入txt
        String yn = native_lib.getHX();
        if(yn ==  "N"){  //  公私钥存入失败
            return;
        }
        //  读取txt文本获取公私钥信息
        pubkey = txtHelper.getline(1);  //  第一行公钥
        seckey = txtHelper.getline(2);  //  第二行私钥
        AESUtilsz aesUtilsz =new AESUtilsz();
        //  生成传至服务器的私钥 由password加密
        try {
            toserverSeckey = aesUtilsz.encrypt(seckey,password1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MD5Encode md5Encode = new MD5Encode();
        toserverPass = md5Encode.MD5EncodeUtf8(password1,"2016061327");  //  加密password1，加盐内容为写死的字符串

        SignTask signtask = new SignTask();
        signtask.execute(name1,toserverPass,pubkey,toserverSeckey);  //  应当将加密后的密码上传服务器
        signtask.setMsgRes(new MessageResponse() {
            @Override
            public void onReceivedSuccess(String id) {
                savetoSqlite(id);  //  保存用户信息至本地
                //  注册后进入登录页面，注册时默认用户不在线，online = N
                jumptoMain();
            }
        });
    }

    private void judgename(final String name1) {
        //  进入服务端判断是否存在该昵称的用户!
        new Thread(){
            public void run(){
                try{
                    addFriendUtils addFri = new addFriendUtils();
                    String id = addFri.getvalue("name",name1,"id"); //  查找name1的id
                    Message msg = handler.obtainMessage();
                    if(!id.equals("")){  // 存在该昵称
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }else{
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        }.start();
    }

    //  存储用户数据至本地数据库
    private void savetoSqlite(String id) {
        SqliteMain sqliteMain = new SqliteMain(this);
        SQLiteDatabase db = sqliteMain.getReadableDatabase();
        sqliteMain.addUserRecord(db,id,name1,password1,"N",pubkey,seckey);

    }
    //  跳转至登录页面
    private void jumptoMain() {
        closeNetty = false;
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(closeNetty){
            client.disconnect(nettyChannelFuture.getChannel());
        }
    }
}
