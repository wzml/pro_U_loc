package com.example.socket_test.netty;

import android.util.Log;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class EchoClientHandler extends SimpleChannelInboundHandler<String> {
    public String msgFromS;
    public boolean getMsg = false;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        //mMainHandler.sendEmptyMessage(Main3Activity.MSG_CONNECT);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String data) throws Exception {
        Log.e("from Server:",data);
        System.out.println("received msg from server:" + data);
        msgFromS = data;
        getMsg = true;
    }

    public String getMsgFromS(){
        if(getMsg){  //  同一数据仅允许读取一次
            getMsg = false;
            return msgFromS;
        }
        return "";
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}