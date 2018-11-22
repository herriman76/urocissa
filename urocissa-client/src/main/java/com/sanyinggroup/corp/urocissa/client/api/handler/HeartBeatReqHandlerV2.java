package com.sanyinggroup.corp.urocissa.client.api.handler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.client.model.MsgHead;
import com.sanyinggroup.corp.urocissa.core.model.MessageType;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
/**
 * 
 * <p>Package:com.sanyinggroup.corp.urocissa.client.api.handler</p> 
 * <p>Title:HeartBeatReqHandlerHeartBeatReqHandlerV2</p> 
 * <p>Description: 心跳检测</p> 
 * @author lixiao
 * @date 2017年8月18日 下午4:53:21
 * @version
 */
@Deprecated
public class HeartBeatReqHandlerV2 extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(HeartBeatReqHandlerV2.class);
	private volatile ScheduledFuture<?> heartBeat;
	//private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private int beatSec = 30; //心跳时间
	/**
	 * 服务器返回信息，登录成功即发送心跳
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) 
			throws Exception {
		MiddleMsg message = (MiddleMsg) msg;
		// 握手成功，主动发送心跳消息
		if (message.getHeader() != null
				&& message.getHeader().getType() == MessageType.LOGIN_RESP
						.value()) {
			//logger.debug("服务器返回登录信息："+msg);
			if(message.getHeader().getStatus()==200 && message.getHeader().getSessionID()!=null && !("").equals(message.getHeader().getSessionID())){
				//heartBeatTime = new Date(); //设置最后一次响应时间
				heartBeat = ctx.executor().scheduleAtFixedRate(
						new HeartBeatReqHandlerV2.HeartBeatTask(this,ctx,message.getHeader().getSessionID()), 0, beatSec,
						TimeUnit.SECONDS);
			}else{
				logger.error((String) (message.getHeader().getStatus()+":"+message.getHeader().getAttachment()!=null?message.getHeader().getAttachment().get("statusMsg"):""));
			}
			ctx.fireChannelRead(msg);
		} else if (message.getHeader() != null
				&& message.getHeader().getType() == MessageType.HEARTBEAT_RESP
						.value()) {
			//heartBeatTime = new Date(); //设置最后一次响应时间
			logger.debug("收到服务器的心跳响应：--->"+message);
		} else
			ctx.fireChannelRead(msg);
	}
	/**
	 * 
	 * <p>Package:com.sanyinggroup.corp.urocissa.client.api.handler</p> 
	 * <p>Title:HeartBeatTask</p> 
	 * <p>Description: 循环发送心跳</p> 
	 * @author lixiao
	 * @date 2017年8月8日 下午5:30:12
	 * @version
	 */
	private class HeartBeatTask implements Runnable {
		private final ChannelHandlerContext ctx;
		private  String sessionID ;
		public HeartBeatTask(final HeartBeatReqHandlerV2 heart, final ChannelHandlerContext ctx,final String sessionID) {
			this.ctx = ctx;
			this.sessionID = sessionID;
		}
		@Override
		public void run() {
				MiddleMsg heatBeat = buildHeatBeat();
				logger.debug("客户端向服务器发送心跳请求：--->"+heatBeat);
				ctx.writeAndFlush(heatBeat).addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						System.out.println("----------");
						logger.debug(future.toString());
						logger.debug(future.isSuccess()+"");
						System.out.println("----------");
						
					}
				});
		}
		private MiddleMsg buildHeatBeat() {
			MiddleMsg message = new MiddleMsg();
			MsgHead header = new MsgHead();
			header.setSessionID(sessionID);
			header.setType(MessageType.HEARTBEAT_REQ.value());
			message.setHeader(header);
			return message;
		}
	}
	/**
	 * 异常处理
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if (heartBeat != null) {
			heartBeat.cancel(true);
			heartBeat = null;
		}
		ctx.fireExceptionCaught(cause);
	}
	
}
