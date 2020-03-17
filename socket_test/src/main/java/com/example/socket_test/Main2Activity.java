package com.example.socket_test;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.socket_test.log.LogFileUtils;
import com.example.socket_test.log.LogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main2Activity extends AppCompatActivity {
    private ConnectivityManager mConnectivityManager = null;
    private NetworkInfo mActiveNetInfo = null;
    private TextView ipTextView;
    private Button tzButton;
    private Switch mSwitch;
    private Handler handler = new Handler();
    private Runnable runnable;

    FileOutputStream outputStream = null;
    BufferedWriter writeBuffer = null;
    Calendar calendar = null;
    Lock lock = new ReentrantLock(true);  //  公平锁


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = new Intent(this,Main3Activity.class);
        startActivity(intent);

        ipTextView = findViewById(R.id.main2_tv);
        ipTextView.setText("txt");
        tzButton = findViewById(R.id.main2_bt);
        mSwitch = findViewById(R.id.main2_sw);
        //  获取手机ip功能
//        mConnectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);//获取系统的连接服务
//        mActiveNetInfo = mConnectivityManager.getActiveNetworkInfo();//获取网络连接的信息
//        if(mActiveNetInfo==null)
//            myDialog();
//        else{
//            final String ip = getIPAddress();
//            new Thread(){
//                public void run(){
//                    try {
//                        toserverip("5",ip);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
//        }
//        setUpInfo();

        //  测试通知功能
        //  测试日志功能
        if(Build.VERSION.SDK_INT >= 23){
            int write = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if(write != PackageManager.PERMISSION_GRANTED || read != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},300);
            }else{
            }
        }
        tzButton.setText("保存日志");
        tzButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btntext = tzButton.getText().toString(); //  button上的文字
                if(btntext.equals("保存日志")){  //  显示邻近通知
                    tzButton.setText("显示日志");
                    //startNotification();
                    String log = "日志测试";

                    //  添加日志
                    LogUtils.i("标题:",log);
                    Toast.makeText(Main2Activity.this,"添加日志成功",Toast.LENGTH_SHORT).show();
                }else{  //  关闭邻近通知
                    tzButton.setText("保存日志");
                    //onDestroy();
                    ipTextView.setText(LogFileUtils.readLogText());
                }
            }
        });

        //  测试switch按钮功能
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //ipTextView.setText("开启");
                    //Toast.makeText(Main2Activity.this,"开启switch!",Toast.LENGTH_SHORT).show();
                    startNotification();
                }else {
                    //ipTextView.setText("关闭");
                    //Toast.makeText(Main2Activity.this,"关闭switch!",Toast.LENGTH_SHORT).show();
                    onDestroy();
                }
            }
        });

        //   测试日志功能
        // Log1("this is test log");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permission,int[] grantResults){
        if(requestCode == 300){
            Log.i("t","dasa");
        }else{

        }
}

    public void Log1(String logContent){
        lock.lock();
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String log_file = String.format("%d-%d-%d.log",year,month,day);

        try{
            outputStream = openFileOutput(log_file, Context.MODE_APPEND);  //  文件不存在则创建
            writeBuffer = new BufferedWriter(new OutputStreamWriter(outputStream));

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            int sec = calendar.get(Calendar.SECOND);
            int msec = calendar.get(Calendar.MILLISECOND);

            String str = String.format("[%04d-%02d-%02d   %02d:%02d:%02d:%03d]      %s\n",year,month,day,hour,min,sec,msec,logContent);
            writeBuffer.write(str);
            writeBuffer.close();
            writeBuffer = null;
        }catch (IOException e){
            e.printStackTrace();
        } finally {
            lock.unlock();  //  解锁
        }
    }

    //  5s一次通知
    private void startNotification() {
        runnable = new Runnable() {
            @Override
            public void run() {
                notMessage();
                handler.postDelayed(this,5*1000);
            }
        };
        handler.postDelayed(runnable,5*1000);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    //  通知信息
    private void notMessage() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelID = "FRader";
        //  高版本需要渠道(Android 8.0及以上版本，若需要发送通知需要配送通知渠道，不然无法成功发送通知)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelID,"FRader",NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        //  点击通知后跳入MainActivity
        Intent intent = new Intent(Main2Activity.this,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(Main2Activity.this,0,intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(Main2Activity.this,channelID);
        builder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.frader))
                .setContentTitle("邻近结果")
                .setContentText("通知内容")
                .setContentIntent(pi)  //  设置通知栏点击跳转
                .setAutoCancel(true);
        manager.notify(0x12,builder.build());
    }

    public String getIPAddress() {
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if ((info.getType() == ConnectivityManager.TYPE_MOBILE) || (info.getType() == ConnectivityManager.TYPE_WIFI) ){//当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                }
                catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }
        else { //当前无网络连接,请在设置中打开网络
            return null;
        }
        return null;
    }

    public void setUpInfo()  {
        if(mActiveNetInfo.getType()==ConnectivityManager.TYPE_WIFI)  {
            //nameTextView.setText("网络类型：WIFI");
            ipTextView.setText("IP地址："+getIPAddress());
        }
        else if(mActiveNetInfo.getType()==ConnectivityManager.TYPE_MOBILE)  {
            //nameTextView.setText("网络类型：3G/4G");
            ipTextView.setText("IP地址："+getIPAddress());
        }
        else  {
            //nameTextView.setText("网络类型：未知");
            ipTextView.setText("IP地址：");
        }
    }

    private void myDialog()  {
        AlertDialog mDialog = new AlertDialog.Builder(Main2Activity.this)
                .setTitle("注意")
                .setMessage("当前网络不可用，请检查网络！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener()  {
                    @Override
                    public void onClick(DialogInterface dialog, int which)  {
                        dialog.dismiss();
                        Main2Activity.this.finish();
                    }
                })
                .create();//创建这个对话框
        mDialog.show();//显示这个对话框
    }

    public void toserverip(String num,String ip) throws IOException {
        Socket socket  = new Socket();
        socket.connect(new InetSocketAddress("118.31.61.122",7327),6000);
        //to server
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        //from server
        InputStreamReader Socin = new InputStreamReader(socket.getInputStream());
        BufferedReader SocBuf = new BufferedReader(Socin);

        pw.write(num+"\n");pw.flush();
        pw.write(ip+"\n");pw.flush();

        os.close();
        Socin.close();
        socket.close();
    }
}
