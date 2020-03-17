package com.example.socket_test.netty;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import static com.example.socket_test.Main3Activity.DATA_SEND;
import static com.example.socket_test.Main3Activity.MSG_SEND;

public class nettyTcpThread extends Thread{
    private String TAG = "nettyTcpClient";
    private String mIp = "118.31.61.122";
    private int mPort = 7327;

    private Channel mChannel;
    private nettySendThread mSendThread;
    private Handler mMainHandler;

    private int reconnectNum = 10;
    private boolean isConnect = false;
    private boolean isNeedReconnet = true;

    private long reconnectIntervalTime = 60*1000;

    @Override
    public void run() {
//        if (isConnect){
//            return;
//        }
        EventLoopGroup meventLoopGroup = new NioEventLoopGroup();
        try{
            Bootstrap clientBootStrap = new Bootstrap();
            clientBootStrap.group(meventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)  //  屏蔽Nagle算法试图
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                    .handler(new ClientInitializer());
            ChannelFuture future = clientBootStrap.connect(mIp, mPort).sync();
            mChannel = future.channel();
            mChannel.writeAndFlush("this msg test com from client" + "\r\n");
            // Wait until the connection is closed.
            mChannel.closeFuture().sync();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            meventLoopGroup.shutdownGracefully();
        }
    }

//    public void connect(){
//        if (isConnect){
//            return;
//        }
//        if (meventLoopGroup == null){
//            //  NIO线程组
//            meventLoopGroup = new NioEventLoopGroup();
//        }
//        new Thread(){
//            @Override
//            public void run(){
//                isNeedReconnet = true;
//                Bootstrap clientBootStrap = new Bootstrap();
//                clientBootStrap.group(meventLoopGroup)
//                        .channel(NioSocketChannel.class)
//                        .option(ChannelOption.TCP_NODELAY,true)  //  屏蔽Nagle算法试图
//                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
//                        .handler(new ChannelInitializer<SocketChannel>() {
//                            @Override
//                            protected void initChannel(SocketChannel ch) throws Exception {
//                                ch.pipeline()
//                                       // .addLast("ping",new IdleStateHandler(0,5,0, TimeUnit.SECONDS))
//                                        .addLast(new StringEncoder(CharsetUtil.UTF_8))
//                                        .addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()))
//                                        .addLast(new StringDecoder(CharsetUtil.UTF_8))
//                                        .addLast(new EchoClientHandler());
//                            }
//                        });
//                try {
//                    // Start the client.
//                    ChannelFuture future = clientBootStrap.connect(mIp, mPort).sync();
//                    mChannel = future.channel();
//                    isConnect = true;
//                    // Wait until the connection is closed.
//                    mChannel.closeFuture().sync();
//                    Log.e(TAG,"断开连接");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                    //  关闭连接
//                    disconnect();
//                    // reconnect();
//                }
//            }
//        }.start();
//    }

    public void disconnect(){
        Log.e(TAG,"disconnect");
        if(mChannel != null){
            mChannel.disconnect();  //  关闭连接
            mChannel.close();
        }
        isConnect = false;
        isNeedReconnet = false;
    }

    private void reconnect() {
        Log.e(TAG,"reconnect");
        if (isNeedReconnet && reconnectNum > 0 && !isConnect){
            reconnectNum--;
            SystemClock.sleep(reconnectIntervalTime);
            if (isNeedReconnet && reconnectNum > 0 && !isConnect){
                Log.e(TAG,"重新连接");

            }
        }
    }

    //  发送数据
    public void sendMsgToServer(String data){
        Handler socketHandler = mSendThread.getSocketHandler();
        Message message = socketHandler.obtainMessage();
        message.what = MSG_SEND;
        Bundle bundle = new Bundle();
        bundle.putString(DATA_SEND, data);
        message.setData(bundle);
        socketHandler.sendMessage(message);
        if(mChannel != null && isConnect && mChannel.isOpen()){
            mChannel.writeAndFlush(data+"\n");
            Log.d(TAG,"send Succeed");
        }
//        try{
//            if(mChannel != null && isConnect && mChannel.isOpen()){
//                mChannel.writeAndFlush(data);
//                Log.d(TAG,"send Succeed");
//            }else {
//                reconnect();
//                throw  new Exception("cannel is null | closed");
//            }
//        }catch (Exception e){
//            reconnect();
//            e.printStackTrace();
//        }
    }

    //  获取TCP连接状态
    public boolean getConnectStatus(){
        return isConnect;
    }

    public void setConnectStatus(boolean status){
        this.isConnect = status;
    }
}
