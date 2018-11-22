package com.sanyinggroup.corp.urocissa.client.api.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

@SuppressWarnings("rawtypes")
public class ChannelPoolHandlerImpl extends SimpleChannelInboundHandler
		implements ChannelPoolHandler {
	@Override
	public void channelReleased(Channel ch) throws Exception {
		System.out.println("channelReleased");
	}

	@Override
	public void channelAcquired(Channel ch) throws Exception {
		System.out.println("channelAcquired");
	}

	@Override
	public void channelCreated(Channel ch) throws Exception {
		System.out.println("channelCreated");
		NioSocketChannel channel = (NioSocketChannel) ch;
		channel.config().setKeepAlive(true);
		channel.config().setTcpNoDelay(true);
		ChannelPipeline pipeline = channel.pipeline();
		ChannelPoolHandlerImpl handler = new ChannelPoolHandlerImpl();
		pipeline.addLast(handler);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub

	}
}
