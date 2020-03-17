package com.example.login_server.loginInfo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.login_server.MyUtils.AESUtilsz;
import com.example.login_server.MyUtils.MD5Encode;
import com.example.login_server.MyUtils.SqliteMain;
import com.example.login_server.MyUtils.manageTxt;
import com.example.login_server.R;
import com.example.login_server.UserInfo.UserActivity;
import com.example.login_server.netty.nettyChannelFuture;
import com.example.login_server.netty.nettyTcpThread;

public class MainActivity extends AppCompatActivity {
    public static MainActivity mactivity;
    public String pubkey,seckey;
    private String TAG = "MainActivity";
    public String mname;
    public String mid;
    public String mpassword;
    public static int RECNAME_LOGIN = 1;
    public static int RECINFO_LOGIN = 2;
    private boolean closeNetty = true;
    private nettyTcpThread client;

    public static MainActivity getMainActivity(){
        return mactivity;
    }

    private Handler msghandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == RECNAME_LOGIN){
                //  获取登录后的响应
                mname = msg.getData().getString("msg");
                Log.i(TAG,"获取的mname="+mname);
                if (mname == null || mname.equals("")){
                    Log.i(TAG,"不存在该用户");
                    Looper.prepare();
                    Toast.makeText(mactivity,"id或密码错误！",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }else{
                    savetoSqliteId(mid);  //  存入用户信息至Sqlite，状态为在线，存到本地的密码自然是明文
                }

            }else if(msg.what == RECINFO_LOGIN){
                //  获取登录后的用户信息
                pubkey = msg.getData().getString("pubkey");
                String seckeyfromServer = msg.getData().getString("seckey");  //  获取私钥
                savetoSqliteInfo(pubkey,seckeyfromServer);
            }
        }
    };


    //  获取用户公钥私钥并存储至SQLite
    private void savetoSqliteInfo(String pubkey,String seckeyfromServer) {
        AESUtilsz aesUtilsz = new AESUtilsz();
        try {  //  解密来自服务器的私钥
            seckey = aesUtilsz.decrypt(seckeyfromServer,mpassword);
            Log.i(TAG,"seckey="+seckey);
        }catch (Exception e){
            e.printStackTrace();
        }
        SqliteMain sqliteMain = new SqliteMain(MainActivity.this);
        SQLiteDatabase db = sqliteMain.getReadableDatabase();
        sqliteMain.addUserRecord(db,mid,mname,mpassword,"Y",pubkey,seckey);
        db.close();
        Log.i(TAG,"存储成功");
        jumptoUser(mid);
    }

    public Handler getMsghandler(){
        return msghandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mactivity = this;
        client = new nettyTcpThread();
        final MD5Encode md5Encode = new MD5Encode();

        Button login = findViewById(R.id.login);
        Button sign = findViewById(R.id.sign);
        final EditText id = findViewById(R.id.id);
        final EditText password = findViewById(R.id.password);

        //  授权读写SD卡txt文本
        getqx();
        //  登录
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"login按钮");
                if(getqx1() == false)
                    return; //  判断授权说明
                manageTxt txtHelper = new manageTxt();
                if(txtHelper.isexit() == false){  //  不存在txt文本的话 创建txt文本
                    if(false == txtHelper.generateTxt())  //  创建失败则无法登录
                        return;
                }
                txtHelper.clearTxt();  //  登录：清空txt原有数据信息。

                mid = id.getText().toString();
                mpassword = password.getText().toString();
                if (mid==null || mid.equals("") || mpassword == null || mpassword.equals("")){
                    Toast.makeText(mactivity,"输入错误",Toast.LENGTH_SHORT).show();
                    return;
                }

                String toserverPass = md5Encode.MD5EncodeUtf8(mpassword,"2016061327");  //  用于服务器判断加密后的密码是否相同,加盐内容已写死
                String data = "req=login;id="+mid+";password="+toserverPass+";";
                client.sendMsg(nettyChannelFuture.getChannel(),data);
            }
        });

        //  进入注册页面
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getqx1() == false)
                    return; //  判断授权说明
                jumptoSign();
            }
        });

    }

    //  online = 'Y',修改本地Sqlite数据库
    private void savetoSqliteId(String id){
        SqliteMain sqliteMain = new SqliteMain(this);
        SQLiteDatabase db = sqliteMain.getReadableDatabase();
        if(sqliteMain.queryExistId(db,id) == true){ //  存在该用户
            sqliteMain.updateUserInfo(db,id,"online","Y");  //  修改在线状态为在线
            db.close();
            jumptoUser(id);
        } else{
            String data = "req=loginmyinfo;id="+id+";";
            client.sendMsg(nettyChannelFuture.getChannel(),data);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(closeNetty){  //  结束aciticity时判断是否结束netty连接
            client.disconnect(nettyChannelFuture.getChannel());
        }
    }

    public void getqx() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                }else{
                    ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
            }
        }
    }

    //  登录按钮授权操作
    public boolean getqx1() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"请打开读写SD卡权限！",Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[],int[] grantResults){
        switch (requestCode){
            case 1:{
                return;
            }
        }
    }

    //  注册页面
    private void jumptoSign(){
        closeNetty = false;
        Intent intent = new Intent(this,SignActivity.class);
        startActivity(intent);
    }

    //  跳转至用户页面
    private void jumptoUser(String id){
        Log.i(TAG,"进入jumptouser");
        closeNetty = false;
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("id",id);  //向下一个页面传参
        startActivity(intent);
    }
}
