package com.example.socket_test;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socket_test.netty.nettyTcpThread;

public class Main3Activity extends AppCompatActivity implements OnClickListener{
    private EditText mIpEt;
    private EditText mPortEt;
    private Button mConnBtn;
    private TextView mScreenTv;
    private EditText mInputEt;
    private Button mSendBtn;
    private Button mCloseBtn;

    private static Handler mMainHandler;

    public static final int MSG_CONNECT = 0x001;
    public static final int MSG_RECEIVE = 0x002;
    public static final int MSG_SEND = 0x003;

    public static final String DATA_RECEIVE = "data_receive";
    public static final String DATA_SEND = "data_send";

    private nettyTcpThread mNettyTcpClient;
    private String TAG = "Main3Activity";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        findViews();
        mNettyTcpClient = new nettyTcpThread();
        mNettyTcpClient.start();
        // TODO handler may cause memory leaks
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_CONNECT:
                        Toast.makeText(Main3Activity.this, "Connect to Server Success", Toast.LENGTH_SHORT).show();
                        mConnBtn.setText("Connected");
                        mConnBtn.setEnabled(false);
                        break;
                    case MSG_RECEIVE:
                        Bundle data = msg.getData();
                        String dataStr = data.getString(DATA_RECEIVE);
                        CharSequence originData = mScreenTv.getText();
                        String result = originData + "\n" + dataStr;
                        mScreenTv.setText(result);
                        break;
                }
            }
        };
    }

    private void findViews() {
        mIpEt = findViewById(R.id.main_ip_et);
        mPortEt = findViewById(R.id.main_port_et);
        mConnBtn = findViewById(R.id.main_connect_btn);
        mScreenTv = findViewById(R.id.main_screen_tv);
        mInputEt = findViewById(R.id.main_input_et);
        mSendBtn = findViewById(R.id.main_send_btn);
        mCloseBtn = findViewById(R.id.main_close_btn);

        // defalut value. Change it to your own server ip
        mIpEt.setText("118.31.61.122");
        mPortEt.setText("7327");
        mConnBtn.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.main_connect_btn:
                String ip = mIpEt.getText().toString();
                String port = mPortEt.getText().toString();
                if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
                    Toast.makeText(Main3Activity.this, "ip or port is null", Toast.LENGTH_SHORT).show();
                } else {
                    connect();
                }
                break;
            case R.id.main_send_btn:
                String data = mInputEt.getText().toString();
                if (!TextUtils.isEmpty(data)) {
                    mNettyTcpClient.sendMsgToServer(data);
                }
                break;
            case R.id.main_close_btn:
                closeconnect();
                break;

        }
    }

    private void connect(){
        Log.d(TAG,"connect");
        if(!mNettyTcpClient.getConnectStatus()){
            mNettyTcpClient.start(); //  连接服务器
        }else{
            Log.d(TAG,"已连接");
        }
        if(mNettyTcpClient.getConnectStatus()){
            mConnBtn.setEnabled(false);
            Toast.makeText(this,"连接成功",Toast.LENGTH_SHORT).show();
        }
    }

    public void closeconnect(){
        if(mNettyTcpClient.getConnectStatus()){
            mNettyTcpClient.disconnect();
        }
        mConnBtn.setEnabled(true);
        Toast.makeText(this,"关闭连接",Toast.LENGTH_SHORT).show();
    }

    public void sendMessage(String data) {
        if (!mNettyTcpClient.getConnectStatus()) {
            Toast.makeText(getApplicationContext(), "未连接，请先连接", Toast.LENGTH_SHORT).show();
        }else{
            mNettyTcpClient.sendMsgToServer(data+"\n");
        }
    }
}
