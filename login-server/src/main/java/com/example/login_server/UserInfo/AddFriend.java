package com.example.login_server.UserInfo;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login_server.MyUtils.SqliteMain;
import com.example.login_server.R;

import java.io.IOException;

public class AddFriend extends Activity {
    private String name = null;
    private String myid = null;
    private String myname = null;
    private String id = null;
    private TextView tv_add_find;
    private EditText et_add_name;
    private RelativeLayout rl_add;
    private TextView tv_add_name;
    private Button bt_add_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        //本机用户id
        Intent intent = getIntent();
        myid = intent.getStringExtra("id"); //接收参数名为username的参数
        SqliteMain sqliteMain = new SqliteMain(this);
        SQLiteDatabase db = sqliteMain.getReadableDatabase();
        myname = sqliteMain.queryUserRecord(db,myid,"name");
        db.close();

        //初始化view
        initView();

        //初始化监听
        initListener();
    }

    private void initListener() {
        //查找按钮的点击事件处理
        tv_add_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
            }
        });

        //添加按钮的点击事件处理
        bt_add_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });
    }

    //查找按钮处理
    private void find() {
        //获取输入的用户id
        id = et_add_name.getText().toString();

        //校验输入名称
        if(TextUtils.isEmpty(id)){
            Toast.makeText(this,"输入的用户账号不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(id.equals(myid) ) {
            Toast.makeText(this,"不能添加当前账号",Toast.LENGTH_SHORT).show();
            return;
        }
        //去服务器判断当前用户是否存在
        new Thread( ){
            @Override
            public void run(){
                try{
                    addFriendUtils adf = new addFriendUtils();
                    String name1[] = {null};
                    name1 = adf.findserver(myname,id,"1");  //  好友账号不能为已有好友账号
                    name = name1[0];
                }catch (IOException e){
                    e.printStackTrace();
                }
                //System.out.println(name);
                //更新UI显示-线程中更改
                if(!name.equals("F")){
                    System.out.println(name);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rl_add.setVisibility(View.VISIBLE);
                            tv_add_name.setText(name);
                        }
                    });
                } else {
                    Looper.prepare(); //子线程不可更改UI显示，需要使用Looper搭配后显示
                    Toast.makeText(AddFriend.this, "账号错误！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }.start();

    }

    //添加按钮处理,myid+id
    private void add() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    addFriendUtils adf = new addFriendUtils();
                    String flag1[] = {null};
                    flag1= adf.findserver(myid,id,"2");
                    String flag = flag1[0];
                    if(flag.equals("T")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddFriend.this,"发送添加好友邀请成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else{
                        Looper.prepare();
                        Toast.makeText(AddFriend.this,"发送添加好友邀请失败",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(AddFriend.this,"发送添加好友邀请失败"+e.toString(),Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }).start();

    }

    private void initView() {
        tv_add_find = (TextView)findViewById(R.id.tv_add_find);
        et_add_name = findViewById(R.id.et_add_name);
        rl_add = findViewById(R.id.rl_add);
        tv_add_name = findViewById(R.id.tv_add_name);
        bt_add_add = findViewById(R.id.bt_add_add);
    }
}
