package com.example.login_server.netty;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.login_server.loginInfo.MainActivity;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
/**
 *2020-3-15
 * by Zhang Liling
 **/
public class EchoClientHandler extends SimpleChannelInboundHandler<String> {
    private String TAG = "EchoClientHandler";
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    private String getmsg(String data,String msg){
        String getmsg = null;
        String[] data0 = data.split(msg+"=");
        if(data0!=null && data0.length > 1){
            String[] data1 = data0[1].split(";");
            getmsg = data1[0];
        }
        return getmsg;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String data) throws Exception {
        //  处理data
        manageData(data);

        Log.i(TAG,"received msg from server:" + data);
    }

    private void manageData(String data) {
        String req="";
        req = getmsg(data,"res");
        if(req.equals("login")){
            String myname = "";
            myname = getmsg(data,"myname");
            Message message = new Message();
            message.what= MainActivity.RECNAME_LOGIN;
            Bundle bundle = new Bundle();
            bundle.putString("msg",myname);
            message.setData(bundle);
            MainActivity.getMainActivity().getMsghandler().sendMessage(message);
        }else if(req.equals("loginmyinfo")){
            Message message = new Message();
            message.what= MainActivity.RECINFO_LOGIN;
            Bundle bundle = new Bundle();
            bundle.putString("pubkey",getmsg(data,"pubkey"));
            bundle.putString("seckey",getmsg(data,"seckey"));
            message.setData(bundle);
            MainActivity.getMainActivity().getMsghandler().sendMessage(message);

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}