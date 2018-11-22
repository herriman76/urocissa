package com.sanyinggroup.corp.urocissa.client.api.channel;



import com.sanyinggroup.corp.urocissa.client.api.handler.ClientHandler;
import com.sanyinggroup.corp.urocissa.client.api.handler.HeartBeatReqHandler;
import com.sanyinggroup.corp.urocissa.client.api.handler.MsgHandler;
import com.sanyinggroup.corp.urocissa.client.codec.MessageDecoder;
import com.sanyinggroup.corp.urocissa.client.codec.MessageEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
/**
 * 
 * <p>Package:com.sanyinggroup.corp.urocissa.client.api.channel</p> 
 * <p>Title:ChildChannelInitializer</p> 
 * <p>Description: channel初始化</p> 
 * @author lixiao
 * @date 2017年7月18日 下午1:41:41
 * @version
 */
@Deprecated
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel>{
	private MsgHandler msgHandler;
	public ClientChannelInitializer(){
		super();
	}
	public ClientChannelInitializer(MsgHandler msgHandler){
		super();
		this.msgHandler = msgHandler;
		
	}
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new MessageDecoder(2000*1024 * 1024, 4, 4)); //消息解码
		pipeline.addLast("MessageEncoder", new MessageEncoder());
		pipeline.addLast("readTimeoutHandler", new ReadTimeoutHandler(120)); // 设置超时
		ch.pipeline().addLast("HeartBeatHandler", new HeartBeatReqHandler());
		pipeline.addLast("handler", new ClientHandler(msgHandler));
	}

}
