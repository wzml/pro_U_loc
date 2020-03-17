package com.example.socket_test.netty;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import io.netty.channel.Channel;

import static com.example.socket_test.Main3Activity.DATA_SEND;
import static com.example.socket_test.Main3Activity.MSG_SEND;

public class nettySendThread extends Thread{
    private Handler mSocketHandler;
    private Channel mchannel;

    public nettySendThread(Channel channel){
        mchannel = channel;
    }

    @Override
    public void run() {
        //super.run();
        if(mSocketHandler == null){
            Looper.prepare();
            mSocketHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(Message msg){
                    switch (msg.what){
                        case MSG_SEND:
                            String data = msg.getData().getString(DATA_SEND);
                            mchannel.writeAndFlush(data+"\n");
                            break;
                    }
                }
            };
        }
        Looper.loop();
    }

    public Handler getSocketHandler(){
        return mSocketHandler;
    }
}
