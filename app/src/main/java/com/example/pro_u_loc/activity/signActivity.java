package com.example.pro_u_loc.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pro_u_loc.utils.DBUtils;
import com.example.pro_u_loc.R;

public class signActivity extends AppCompatActivity {
    private static final String TAG = "signActivity";

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.what == 1) {  //数据插入成功，进入用户页面
                showdiasuc(msg.obj.toString()); //跳转页面
            } else {
                Toast.makeText(signActivity.this, "数据存储失败！", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("id"); //接收参数名为id的参数
        ((TextView)findViewById(R.id.id_show)).setText(id); //显示申请的id

        final EditText name = findViewById(R.id.name);
        final EditText password  = findViewById(R.id.pass);
        final EditText password1 = findViewById(R.id.pass1);
        Button sign1 = findViewById(R.id.zhuce);
        sign1.setOnClickListener(new View.OnClickListener() {  //注册信息
            @Override
            public void onClick(View v) {
                //判断昵称是否为空
                name.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (name.getText().toString().trim() == null) {
                            Toast.makeText(signActivity.this, "昵称不能为空！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        if (name.getText().toString().trim() == null) {
//                            Toast.makeText(signActivity.this, "昵称不能为空！", Toast.LENGTH_SHORT).show();
//                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (name.getText().toString().trim() == null) {
                            Toast.makeText(signActivity.this, "昵称不能为空！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //判断密码是否为空
                password.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        if (password.getText().toString().trim() == null) {
                            Toast.makeText(signActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //判断两次密码是否一致
                password1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        String s1 = password.getText().toString().trim();
                        String s2 = password1.getText().toString().trim();
                        if(!s1.equals(s2)) { //不同
                            Toast.makeText(signActivity.this, "密码不一致！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                final String name1 = name.getText().toString().trim();
                final String pass = password.getText().toString().trim();
                final String pass1 = password1.getText().toString().trim();
                if(check_username(name1) && check_password(pass,pass1)) { //注册信息格式正确
                    //数据插入数据库
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int insert = DBUtils.insertmessage(id,name1,pass);
                            Message msg = new Message();
                            if(insert == 1) { //插入成功
                                msg.what = 1;
                                msg.obj = name1;
                            } else {
                                msg.what = 0;
                            }
                            handler.sendMessage(msg);
                        }
                    }).start();

                } else { //注册信息有误
                    Toast.makeText(signActivity.this, "注册信息错误！", Toast.LENGTH_SHORT).show();
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(signActivity.this);
//                    dialog.setTitle("提示：");
//                    dialog.setMessage("注册信息有误！");
//                    dialog.setCancelable(false);
//                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(signActivity.this,signActivity.class);
//                            startActivity(intent);
//                        }
//                    });
//                    dialog.show();
                }
            }

        });


    }

    //显示注册成功提示，可跳转至登录页面或用户页面
    private void showdiasuc(final String name3) {  //注册成功显示
        AlertDialog.Builder dialog = new AlertDialog.Builder(signActivity.this);
        dialog.setTitle("提示：");
        dialog.setMessage("注册成功");
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(signActivity.this, activity_user.class);
                intent.putExtra("username",name3);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(signActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }
    //判断Name格式是否正确
    private boolean check_username(String name) {
        if(name == null || name.equals("")) {
            return false;
        } else {  //name不为空
            return true;
        }
    }
    //判断密码格式是否正确
    private  boolean check_password(String pass1,String pass2) {
        if(pass1 != null && pass1.equals(pass2)) {
            return true;
        } else {
            return false;
        }
    }

}
