package com.example.login_server.netty;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
/**
 *2020-3-15
 * by Zhang Liling
 **/
public class nettyChannelFuture {
    private static Channel mchannel;
    private static EventLoopGroup mgroup;
    private static boolean misConnect;

    public static void setIsConnect(boolean isConnect) {
        misConnect = isConnect;
    }

    public static boolean getIsConnect() {
        return misConnect;
    }

    public static void setGroup(EventLoopGroup group) {
        mgroup = group;
    }

    public static void setChannel(Channel channel) {
        mchannel = channel;
    }

    public static Channel getChannel(){
        return mchannel;
    }

    public static EventLoopGroup getGroup(){
        return mgroup;
    }
}
