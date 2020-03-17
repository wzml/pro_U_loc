package com.example.login_server.UserInfo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.login_server.MyUtils.GPSGetNewLoc;
import com.example.login_server.MyUtils.SqliteMain;
import com.example.login_server.MyUtils.log.LogUtils;
import com.example.login_server.MyUtils.manageTxt;
import com.example.login_server.MyUtils.native_lib;
import com.example.login_server.R;
import com.example.login_server.UserInfo.fri_list.Fri;
import com.example.login_server.UserInfo.fri_list.FriAdaopter;
import com.example.login_server.loginInfo.MainActivity;
import com.example.login_server.service_saveLoc.Service_sentLocTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UserActivity extends AppCompatActivity {
    private ListView lvfri;
    boolean stopThread = false;
    public static UserActivity userActivity;
    public static Context userContext;
    private FriAdaopter friAdaopter;
    private GPSGetNewLoc gpsGetNewLoc;
    private List<Fri> friList = new ArrayList<Fri>();  //创建集合保存好友信息

    //  获取定位信息相关
    //Intent intent;
    Intent intent1;
    LocationManager lm;
    ServiceConnection conn = null;
    final static int LOCATION_SETTING_REQUEST_CODE = 100;

    addFriendUtils socketHelper = new addFriendUtils();

    ArrayList friNameList = new ArrayList();

    private String id;  //  当前用户id
    private String myname;
    private String[] names = new String[10];
    private ImageView iv_contact_red;
    private Switch mSwitch;
    private Handler handler = new Handler();
    private Runnable runnable;
    private String swRes;
    private LinearLayout ll_contact_invite;
    public String[] res = new String[10]; //好友申请表，最后一个为F

    private Timer timer;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                //充复执行的代码
                //Toast.makeText(MainActivity.this,"演示",Toast.LENGTH_LONG).show();
                //获取好友邀请信息

                if (!stopThread) {
                    new Thread() {
                        public void run() {
                            try {
                                //addFriendUtils adf = new addFriendUtils();
                                res = socketHelper.findserver(id, null, "4");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                int i = 0;
                String namep = "F";
                if (res[i] != null)
                    namep = res[i];
                if (!namep.equals("F")) {
                    i++;
                }
                if (i == 0) { //没有好友邀请
                    iv_contact_red.setVisibility(View.GONE);//默认不显示，如果显示就是visible
                } else {
                    iv_contact_red.setVisibility(View.VISIBLE);
                }

            } else if (msg.what == 1) {  //  获取switch结果
                swRes = (String) msg.obj;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        userActivity = this;
        userContext = this;

        gpsGetNewLoc = new GPSGetNewLoc();

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.inflateMenu(R.menu.user_menu);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setOnMenuItemClickListener(new MyMenuItemClickListener());
        }

        final Intent intent = getIntent();
        id = intent.getStringExtra("id"); //接收参数名为username的参数
        System.out.println(id);
        TextView tv_head_name = findViewById(R.id.user_tv_headname);
        SqliteMain sqliteMain = new SqliteMain(this);
        SQLiteDatabase db = sqliteMain.getReadableDatabase();
        myname = sqliteMain.queryUserRecord(db, id, "name");
        db.close();
        tv_head_name.setText(id + myname);
        mSwitch = findViewById(R.id.user_sw_jclose);  //  switch按钮

        // switch按钮点击事件
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startNotification();
                } else {
                    handler.removeCallbacks(runnable);
                }
            }
        });

        //好友信息显示
        lvfri = (ListView) findViewById(R.id.list_fri); //获得子布局
        getData();
        initAdaopter();
        FriAdaopter friAdaopter = new FriAdaopter(UserActivity.this, R.layout.listview_item, friList); //关联数据和子布局
        lvfri.setAdapter(friAdaopter);  //绑定数据和适配器

        //好友列表项点击事件
        lvfri.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Fri fri = friList.get(position);
                Toast.makeText(UserActivity.this, fri.getName(), Toast.LENGTH_LONG).show();
            }
        });

        //获得红点对象
        iv_contact_red = findViewById(R.id.iv_contact_red);
        iv_contact_red.setVisibility(View.GONE);//默认不显示，如果显示就是visible
        initView(); //加载布局----好友邀请显示
        // 获取邀请信息条目对象
        ll_contact_invite = findViewById(R.id.ll_contact_invite);
        //邀请信息条目点击事件
        ll_contact_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //红点处理--处理事件后红点消失
                iv_contact_red.setVisibility(View.GONE);
                //跳转到邀请信息列表页面
                Intent intent1 = new Intent(UserActivity.this, InviteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArray("inviteName", res);//传递数组res
                bundle.putString("myid", id);//传递当前用户id
                intent1.putExtras(bundle);
                startActivity(intent1);

                refresh();
            }
        });

        //动态申请危险权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //onRequestPermissionsResult();  为了请求权限，为了让权限授予时立即生效，一般需要重写权限回调方法
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            prepareLocatingService();
        }

        serviceStart();
    }

//    @Override  //暂停activity时将stopthread置true，将结束线程
//    protected void onPause() {
//        stopThread=true;  //结束timer里面的线程
//        //timer.cancel();   //销毁timer
//        super.onPause();
//    }

    //  5s一次通知
    private void startNotification() {
        runnable = new Runnable() {
            @Override
            public void run() {
                notMessage();
                handler.postDelayed(this, 10 * 1000);
            }
        };
        handler.postDelayed(runnable, 10 * 1000);
    }

    //  通知信息
    private void notMessage() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelID = "FRader";
        //  高版本需要渠道(Android 8.0及以上版本，若需要发送通知需要配送通知渠道，不然无法成功发送通知)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, "FRader", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        //  获取邻近结果
        manageTxt txtHelper = new manageTxt();
        String myctr = txtHelper.getTime();
        String jContent = judgeclose(myname, myctr, false); //  不需要alert

        //  点击通知后跳入UserActivity,并弹出alert提示框
        Intent intent = new Intent(UserActivity.this, UserNotiActivity.class);
        intent.putExtra("cRes", jContent);
        PendingIntent pi = PendingIntent.getActivity(UserActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);  //  修改最后一个参数使得intent传参成功

        NotificationCompat.Builder builder = new NotificationCompat.Builder(UserActivity.this, channelID);
        builder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.frader))
                .setContentTitle("邻近结果")
                .setContentText(jContent)
                .setContentIntent(pi)  //  设置通知栏点击跳转
                .setAutoCancel(true);
        manager.notify(1, builder.build());

    }

    @Override
    protected void onStart() {
        stopThread = false;
        super.onStart();
    }

    @Override          //销毁activity时将stopthread置true，将结束线程
    protected void onDestroy() {
        super.onDestroy();
        stopThread = true;  //结束timer里面的线程
        timer.cancel();   //销毁timer
        if (intent1 != null) {
            unbindService(conn);  // 解除绑定
            stopService(intent1);
        }
        handler.removeCallbacks(runnable);
        gpsGetNewLoc.stop();
        finish();
    }

    private void initAdaopter() {
        friAdaopter = new FriAdaopter(this, R.drawable.show_fri, friList);
    }

    private void refresh() {
        //获得数据库所有好友列表信息
        getData();
        //刷新适配器
        friAdaopter.refresh(friList);
    }


    //周期性执行线程，延迟0，周期：60*1000=1min即是60s跑一次线程
    private void initView() {   //红点对象是否显示
        //红点是否显示取决于是否有好友邀请信息-----需要每隔一段时间就跑跑线程查看好友邀请信息
        //每隔1min扫描一下红点对象是否显示
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 0;
                mHandler.sendMessage(message);
            }
        }, 0, 60 * 1000);
    }

    private void getData() {
        while (names[0] == null)  //最差都有一个F-------跑线程来获取好友名称
        {
            findfris("3");
        }
        int[] imageIds = {
                R.drawable.show_fri, R.drawable.show_fri,
                R.drawable.show_fri, R.drawable.show_fri,
                R.drawable.show_fri, R.drawable.show_fri,
                R.drawable.show_fri, R.drawable.show_fri,
                R.drawable.show_fri, R.drawable.show_fri
        };
        int i = 0;
        //System.out.println(names[0]);
        //System.out.println(names[1]);
        String namep = "F";
        if (names[i] != null)
            namep = names[i];
        while (!namep.equals("F")) {
            i++;
            namep = names[i];
        }
        int len;
        len = i; //好友长度不超过10个好友
        for (i = 0; i < len; i++) {  //将数据添加到集合中
            //System.out.println("第"+i+"个:--"+names[i]+"--");
            friList.add(new Fri(imageIds[i], names[i]));  //将图片id和对应name存储到一起
        }
        //添加新的好友后可以再friList.add(newXXX);

    }

    private void findfris(final String num) {
        new Thread() {
            public void run() {
                try {
                    //addFriendUtils adf = new addFriendUtils();
                    if (num.equals("3")) {  // 查找好友信息
                        names = socketHelper.findserver(id, null, "3");
                    } else if (num.equals("4")) {   //查看好友添加请求信息,获得被邀请好友姓名列表
                        res = socketHelper.findserver(id, null, num);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    //  开启服务
    private void serviceStart() {
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //  获取代理对象
                Service_sentLocTag.idBinder idBinder = (Service_sentLocTag.idBinder) service;
                //  调用代理方法
                idBinder.MyMethod();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //  断开服务连接
            }
        };
        intent1 = new Intent(getApplicationContext(), Service_sentLocTag.class); // 显示调用服务意图
        intent1.putExtra("id", id);
        System.out.println("MainAc+serStart：" + id);
        intent1.setPackage("com.example.login_server.service_saveLoc");
        bindService(intent1, conn, BIND_AUTO_CREATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class MyMenuItemClickListener implements Toolbar.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.addfriends://点击了添加好友
                    Intent intent = new Intent(UserActivity.this, AddFriend.class);
                    intent.putExtra("id", id);  //向下一个页面传参
                    startActivity(intent);
                    break;
                case R.id.judclose: //判断是否邻近
                    manageTxt txtHelper = new manageTxt();
                    String myctr = txtHelper.getTime();
                    judgeclose(myname, myctr, true);
                    break;
                case R.id.menu_viewLog:
                    toshowLog();
                    break;
                case R.id.back: //退出，回到登录页面--考虑实现服务器退出（通知服务器登录及退出以实现前面的异步通信）
                    //Toast.makeText(UserActivity.this,"back",Toast.LENGTH_SHORT).show();
                    updateSqlite(id);  //  用户修改在线状态
                    stopThread = true;  //结束timer里面的线程
                    timer.cancel();   //销毁timer
                    finish();  //结束当前页面
                    intent = new Intent(UserActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
            }
            return true;
        }

    }

    //  查看日志文档，进入新的activity
    private void toshowLog() {
        Intent intent = new Intent(UserActivity.this, UserNotiActivity.class);
        intent.putExtra("cRes", "log");  //向下一个页面传参
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //  进入UserActivity函数空间后如果点击退出将不会回到flash.activity,因为清除了进程空间所有activity
        startActivity(intent);
    }

    //  判断好友是否邻近--每进入一次便存一次日志
    private String judgeclose(final String myName, final String myctr, final boolean alert) {
        //  获取当前位置tag-a
        SqliteMain sqliteMain = new SqliteMain(UserActivity.this);
        SQLiteDatabase db = sqliteMain.getReadableDatabase();
        String tagA = sqliteMain.queryUserRecord(db, id, "tag");
        db.close();
        Log.v("getTag", tagA);

        if (tagA.equals("F")) { //  如果坐标一直没有获取到时
            //tagA = sentLocTag.getTagtoActivity();
            swRes = "未获取到您当前位置，请稍等！";
            judlocShowDialog(alert, swRes);
            return swRes;
        }
        //  tagA此时有值了
        final String finalTagA = tagA;
        //final String finalTagA = "27";
        new Thread() {
            public void run() {
                try {
                    friNameList = socketHelper.getFriName(myName);  //   获取在线好友昵称
                } catch (IOException e) {
                    e.printStackTrace();
                    //  return;
                }
                Message msg = new Message();
                msg.what = 1;
                int numOfName = friNameList.size();
                ArrayList k12 = new ArrayList();
                if (numOfName != 0) {  //  存在好友在线
                    //  获取所有在线好友的k值，存储模式为k1,k2,k1.....
                    try {
                        k12 = socketHelper.getFriK(myctr, friNameList);  //  获取在线好友的k值
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (k12.size() != 0) {  //  有k12的值
                        //   所有好友邻近结果
                        final String showinfo = getCloseRes(k12, finalTagA);
                        if (showinfo != "") {  //  存在好友邻近
                            msg.obj = showinfo + "与您邻近";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    judlocShowDialog(alert, showinfo + "与您邻近");
                                }
                            });
                        } else {
                            msg.obj = "不存在好友与您邻近！";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    judlocShowDialog(alert, "不存在好友与您邻近！");
                                }
                            });
                        }
                    } else {  //  所有好友还未上传位置信息
                        msg.obj = "不存在好友与您邻近！";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                judlocShowDialog(alert, "不存在好友与您邻近！");
                            }
                        });
                    }
                } else { // 没有一个好友在线或者用户没有一个好友！
                    //  子线程中更新UI
                    msg.obj = "不存在好友与您邻近！";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            judlocShowDialog(alert, "不存在好友与您邻近！");
                        }
                    });
                }
                mHandler.sendMessage(msg);
            }
        }.start();
        LogUtils.i("邻近结果", swRes);
        return swRes;
    }

    private String getCloseRes(ArrayList k12, String tag) {
        //ArrayList close = new ArrayList();
        String showinfo = "";
        manageTxt txtHelper = new manageTxt();
        for (int i = 0; i < k12.size(); i++) {  //  判断所有好友邻近结果
            if (k12.get(i).toString().equals("N")) {  //  当前好友不临近--因为未获取到k1,k2
                i++;
                //close.add("N");
            } else {  //  进入cpp判断
                txtHelper.clearTxt();                          //  清空txt
                txtHelper.writeToFile(k12.get(i).toString());  //  存入K1
                i++;
                txtHelper.writeToFile(k12.get(i).toString());  //  存入k2
                txtHelper.writeToFile(tag);                    //  存入tag
                if (native_lib.judClose().equals("Y")) {  //  邻近
                    showinfo += friNameList.get((i - 1) / 2).toString();  //  加入邻近好友姓名
                    showinfo += "  ";
                    //close.add("Y");
                } else {
                    //close.add("N");
                }
            }

        }

        return showinfo;
    }

    public void updateSqlite(final String id) {
        //  客户端
        //sqliteMain = SqliteMain.getInstance(this);
        SqliteMain sqliteMain = new SqliteMain(this);
        SQLiteDatabase db = sqliteMain.getReadableDatabase();
        sqliteMain.updateUserInfo(db, id, "online", "N");
        db.close();
        //  服务端,  因为是socket，所以要跑一跑子线程
        new Thread() {
            public void run() {
                try {
                    //addFriendUtils addutils = new addFriendUtils();
                    socketHelper.updatevalue("id", id, "online", "N");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //  邻近判断结果showAlertDialog,只有alert为true才显示
    private void judlocShowDialog(boolean alert, String showinfo) {
        if (alert == true) {
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(UserActivity.userActivity);
            dialog.setTitle("邻近判断结果：");
            dialog.setMessage(showinfo);
            dialog.setPositiveButton("OK", new okClick());
            dialog.create();
            dialog.show();
        }
    }

    class okClick implements DialogInterface.OnClickListener{
        public void onClick(DialogInterface dialog,int which){
            dialog.cancel();
        }
    }


    /**
     *  获取GPS位置信息相关函数
     **/

    //  和GPSGetNewLoc搭配使用
    public void getLocationPrepare() {
        if (Build.VERSION.SDK_INT >= 23
                && ActivityCompat.checkSelfPermission(UserActivity.userActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserActivity.userActivity, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1);
            return;
        } else {
            gpsGetNewLoc.prepareLocationService();
        }
        if (ActivityCompat.checkSelfPermission(UserActivity.userActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(UserActivity.userActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserActivity.userActivity, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1);
            return;
        } else {
            gpsGetNewLoc.prepareLocationService();
        }
    }

    //  和getLocationPrepare搭配使用
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prepareLocatingService();  //  判断是否开启GPS信息
                } else {
                    Toast.makeText(this, "没有定位权限！", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    //  Dialog显示判断是否开启GPS位置信息
    private void prepareLocatingService() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("消息框")
                .setMessage("请先打开定位服务")
                .setNegativeButton("已开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gpsGetNewLoc.prepareLocationService();  //  开启位置服务后进入GPSGetNewLoc获取位置信息
                        dialog.cancel();
                    }
                })
                .setPositiveButton("开启服务", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //定位服务设置意图
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, LOCATION_SETTING_REQUEST_CODE);
                    }
                }).show();
    }

    //  有返回值调用的回调 和prepareLocatingService搭配使用
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SETTING_REQUEST_CODE) {
            prepareLocatingService();
        }
    }

}