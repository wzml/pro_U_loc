package com.example.login_server.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
/**
 *2020-3-15
 * by Zhang Liling
 **/
public class ClientInitializer extends ChannelInitializer {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline
                .addLast("framer",new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                .addLast("decoder",new StringDecoder(CharsetUtil.UTF_8))
                .addLast("encoder",new StringEncoder(CharsetUtil.UTF_8))
                .addLast("handler",new EchoClientHandler());
    }
}
