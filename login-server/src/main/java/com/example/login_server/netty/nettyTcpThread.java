package com.example.login_server.netty;

import android.util.Log;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
*2020-3-15
* by Zhang Liling
**/
public class nettyTcpThread {
    private String TAG = "nettyTcpClient";
    private String mIp = "118.31.61.122";
    private int mPort = 7327;

    private Channel mChannel;
    private EventLoopGroup group;

    public void startConnect(){
        if (nettyChannelFuture.getIsConnect()){
            return;
        }
        group = new NioEventLoopGroup();
        try{
            Bootstrap clientBootStrap = new Bootstrap();
            clientBootStrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)  //  屏蔽Nagle算法试图
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                    .handler(new ClientInitializer());
            ChannelFuture future = clientBootStrap.connect(mIp, mPort).sync();
            mChannel = future.channel();
            nettyChannelFuture.setChannel(mChannel);
            nettyChannelFuture.setGroup(group);
            nettyChannelFuture.setIsConnect(true);
            Log.i(TAG,"已连接到服务器！");

            mChannel.closeFuture().sync();
            Log.i(TAG,"已从服务器断开");
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

    //  发起连接&发送数据data
    public void startConnectSendData(String data){
        if (nettyChannelFuture.getIsConnect()){
            return;
        }
        group = new NioEventLoopGroup();
        try{
            Bootstrap clientBootStrap = new Bootstrap();
            clientBootStrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)  //  屏蔽Nagle算法试图
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                    .handler(new ClientInitializer());
            ChannelFuture future = clientBootStrap.connect(mIp, mPort).sync();
            mChannel = future.channel();
            nettyChannelFuture.setChannel(mChannel);
            nettyChannelFuture.setGroup(group);
            nettyChannelFuture.setIsConnect(true);
            Log.i(TAG,"已连接到服务器！");
            mChannel.writeAndFlush(data+"\n");

            mChannel.closeFuture().sync();
            Log.i(TAG,"已从服务器断开");
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

    public void sendMsg(Channel channel, final String data){
        if(channel != null){
            channel.writeAndFlush(data+"\n");
        }else{
            new Thread(){
                @Override
                public void run() {
                    startConnectSendData(data);
                }
            }.start();
        }
    }

    public void disconnect(Channel channel){
        if(channel != null){
            channel.close();
            Log.e(TAG,"disconnect");
            nettyChannelFuture.setIsConnect(false);
        }else{
            Log.i(TAG,"channel已断开连接");
        }
    }

}
