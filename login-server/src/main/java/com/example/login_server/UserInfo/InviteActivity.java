package com.example.login_server.UserInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.login_server.R;
import com.example.login_server.UserInfo.fri_list.Fri;
import com.example.login_server.UserInfo.fri_list.InviteAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InviteActivity extends Activity {
    private ListView lv_invite;
    private String myid;
    private String res  = "F";
    private String INVITE_CHANGE;
    private  LocalBroadcastManager mLBM;
    private String names[] = new String[10];
    private InviteAdapter inviteAdapter;
    private List<Fri> invatationInfos = new ArrayList<Fri>();  //创建集合保存好友信息
    private InviteAdapter.OnInviteLister mOninviteLister = new InviteAdapter.OnInviteLister() {
        @Override
        public void onAccept(Fri invatationInfo) {
            //去服务器告诉好友邀请信息状态，并且当前用户好友列表添加该好友,myid和
            final String name = invatationInfo.getName(); //好友姓名
            //接受好友--conDi = yes
            addFriendUtils adf = new addFriendUtils();
            new Thread(){
                public void run() {
                    try{
                        addFriendUtils adf = new addFriendUtils();
                        res = adf.invite(myid,name,11);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }.start();
            //页面变化
            if(res.equals("T")) {
                Toast.makeText(InviteActivity.this,"接受了邀请", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(InviteActivity.this,"接受邀请失败", Toast.LENGTH_SHORT).show();
            }
            //刷新页面
            refresh();
        }

        @Override
        public void onReject(Fri invatationInfo) {//获得操作结果
            //拒绝--conDi = no
            final String name = invatationInfo.getName();
            addFriendUtils adf = new addFriendUtils();
            new Thread(){
                public void run() {
                    try{
                        addFriendUtils adf = new addFriendUtils();
                        res = adf.invite(myid,name,11);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }.start();
            if(res.equals("T")){
                Toast.makeText(InviteActivity.this,"拒绝了邀请",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(InviteActivity.this,"拒绝邀请失败", Toast.LENGTH_SHORT).show();
            }

            //刷新页面
            refresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        //获取邀请好友列表
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        names = bundle.getStringArray("inviteName");
        myid = bundle.getString("myid");
        //initinvatations();
        initView();
        initDate();
    }

    private void initinvatations() {
        new Thread(){
            public void run() {
                try{
                    addFriendUtils adf = new addFriendUtils();
                    names = adf.findserver(myid,null,"4");
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }.start();
        int imageIds = R.drawable.new_friends_icon;
        int i = 0;
        String namep = "F";
        if(names[i] != null)
            namep = names[i];
        while(!namep.equals("F")){
            i++;
            namep = names[i];
        }
        int len;
        len = i; //好友长度不超过10个好友
        //String conDi = "besent"; //默认为请求添加好友
        //addFriendUtils adf = new addFriendUtils();
        for( i = 0;i < len;i++){  //将数据添加到集合中
            //namep = names[i];
//            try {
//                conDi = adf.findCondi(myid,names[i]);
//            }catch (IOException e){
//                e.printStackTrace();
//            }
            invatationInfos.add(new Fri(imageIds,names[i]));  //将图片id和对应name
        }
    }

    private void initDate() {
        //初始化listview
        inviteAdapter = new InviteAdapter(this,mOninviteLister);
        lv_invite.setAdapter(inviteAdapter);

        //刷新方法
        refresh();
        //注册邀请信息变化的广播
        //mLBM = LocalBroadcastManager.getInstance(this); //联系人邀请信息变化
        //mLBM.registerReceiver(ContactChangedReceiver,new IntentFilter(ina));
    }

    private void refresh() {
        //获取数据库中的所有邀请信息
        initinvatations();
        //刷新适配器
        inviteAdapter.refresh(invatationInfos);
    }

    private void initView() {
        lv_invite = (ListView) findViewById(R.id.lv_invite);
    }
}
