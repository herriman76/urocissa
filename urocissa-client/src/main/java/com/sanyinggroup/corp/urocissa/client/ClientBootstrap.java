package com.sanyinggroup.corp.urocissa.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.client.api.channel.ClientChannelInitializer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
/**
 * 
 * <p>Package:com.sanyinggroup.communication.client</p> 
 * <p>Title:ClientBootstrap</p> 
 * <p>Description: </p> 
 * @author lixiao
 * @date 2017年8月1日 下午2:52:25
 * @version
 */
@Deprecated
public class ClientBootstrap {

	private static final Logger logger = LoggerFactory
			.getLogger(ClientBootstrap.class);
	static Bootstrap bootstrap = new Bootstrap();
	private static ChannelFutureListener channelFutureListener = null;
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private ChannelFuture future;

	private ClientBootstrap() {
		super();
	}

	private void start(final ClientBootstrap client, final String host,
			final int port) throws InterruptedException {
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.group(eventLoopGroup);
		bootstrap.remoteAddress(host, port);
		bootstrap.handler(new ClientChannelInitializer());
		// future 加入监听
		channelFutureListener = new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (future.isSuccess()) {
					client.future = future;
					executor.execute(new Runnable() {
						@Override
						public void run() {
							try {
								while (true) {
									TimeUnit.SECONDS.sleep(5);
									logger.debug(client.future.toString());
								}
							} catch (InterruptedException e) {
							}
						}
					});
					// socketChannel = (SocketChannel) future.channel();
					logger.info("连接成功");
				} else {
					logger.info("连接服务器失败,开始重连服务器");
					// 3秒后重新连接
					future.channel().eventLoop().schedule(new Runnable() {
						public void run() {
							client.doConnect(host, port);
						}
					}, 3, TimeUnit.SECONDS);
				}
			}
		};
		try {
			bootstrap.connect(host, port).sync()
					.addListener(channelFutureListener);
		} finally {
			doConnect(host, port);
			logger.debug("-------------------");
		}

	}

	public void doConnect(final String host, final int port) {
		logger.debug("重新连接中。。。");
		try {
			future = bootstrap.connect(host, port).sync()
					.addListener(channelFutureListener);
		} catch (Exception e) {
			e.printStackTrace();
			if (future == null) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							TimeUnit.SECONDS.sleep(3);
							try {
								doConnect(host, port);// 发起重连操作
							} catch (Exception e) {
								e.printStackTrace();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
			}
			logger.debug("重新连接服务器失败");
		}

	}

	public ChannelFuture getChannelFuture() {
		return future;
	}

	public static ClientBootstrap init(String host, int port)
			throws InterruptedException {
		ClientBootstrap client = new ClientBootstrap();
		client.start(client, host, port);
		
		client.getChannelFuture().channel().writeAndFlush("");
		return client;
	}

	public static void main(String[] args) throws InterruptedException {
		ClientBootstrap client = ClientBootstrap.init("127.0.0.1", 9166);

		logger.debug(client.getChannelFuture().toString());
	}

}
