package com.sanyinggroup.corp.urocissa.server;

import java.io.IOException;

import com.sanyinggroup.corp.urocissa.server.api.handler.ServiceHandler;
import com.sanyinggroup.corp.urocissa.server.codec.MessageDecoder;
import com.sanyinggroup.corp.urocissa.server.codec.MessageEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;



public class ServerTest {
	public void bind() throws Exception {
		// 配置服务端的NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 100)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch)
							throws IOException {
						ch.pipeline().addLast(
								new MessageDecoder(1024 * 1024, 4, 4));
						ch.pipeline().addLast(new MessageEncoder());
						ch.pipeline().addLast("readTimeoutHandler",
								new ReadTimeoutHandler(5));
						ch.pipeline().addLast("handler", new ServiceHandler());
						/*
						 * ch.pipeline().addLast(new LoginAuthRespHandler());
						 * ch.pipeline().addLast("HeartBeatHandler", new
						 * HeartBeatRespHandler());
						 */
					}
				});

		// 绑定端口，同步等待成功
		b.option(ChannelOption.SO_KEEPALIVE, true);
		try {
			ChannelFuture future = b.bind(9002).sync();

			future.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
		System.out.println("Netty server start ok :9002 ");
	}

	/*public static void main(String[] args) throws Exception {
		new ServerTest().bind();
	}*/
}
