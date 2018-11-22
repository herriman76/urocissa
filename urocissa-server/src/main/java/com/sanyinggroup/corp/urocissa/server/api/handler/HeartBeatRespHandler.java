package com.sanyinggroup.corp.urocissa.server.api.handler;

import java.net.InetSocketAddress;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.core.model.MessageType;
import com.sanyinggroup.corp.urocissa.core.util.SecretManagement;
import com.sanyinggroup.corp.urocissa.server.ServerGlobal;
import com.sanyinggroup.corp.urocissa.server.api.model.MiddleMsg;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
/**
 * 
 * <p>Package:com.sanyinggroup.communication.server.api.handler</p> 
 * <p>Title:HeartBeatRespHandler</p> 
 * <p>Description: 心跳检测</p> 
 * @author lixiao
 * @date 2017年7月26日 下午3:44:24
 * @version
 */
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(HeartBeatRespHandler.class);
	/**
	 * 心跳检测
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		MiddleMsg message = (MiddleMsg) msg;
		// 返回心跳应答消息
		if (message.getHeader() != null
				&& message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
			String ip="";
			//logger.info(Logger.getRootLogger().getLevel().getSyslogEquivalent()); //当前日志级别
			//logger.info(Level.DEBUG.getSyslogEquivalent()); //debug 日志级别
			//if(Logger.getRootLogger().getLevel().getSyslogEquivalent()>= Level.DEBUG.getSyslogEquivalent()){
			if(logger.isDebugEnabled()){
				InetSocketAddress address = (InetSocketAddress) ctx.channel()
						.remoteAddress();
				ip = address.getAddress().getHostAddress();
			}
			//logger.debug("接收到来自 :"+ip+"的心跳请求 ---> "+ message);
			MiddleMsg heartBeat = buildHeatBeat(message);
			logger.debug("接收到来自 :"+ip+"的心跳请求 ---> "+ message);
			//先面一行是为了更新心跳时间
			//ServerGlobal.sessionWithAppKeys.put(message.getMsgHead().getSessionID(), message.getMsgHead() );
			try {
				ServerGlobal.sessionWithAppKeys.get(message.getMsgHead().getSessionID())
				 	.setTimestamp(message.getMsgHead().getTimestamp());
			} catch (Exception e) {
				logger.error("此session断开中...");
			}
			//在这儿更换密码
			SecretManagement man = ServerGlobal.sessionWithAppKeys.get(message.getHeader().getSessionID());
			if(man!=null){
				if((new Date().getTime() - man.getChangingTime().getTime())>man.getExpiresSecs()/2 ){
					heartBeat.setBody(man.getNextSecret());
				}
			}
			ctx.writeAndFlush(heartBeat);
		} else
			ctx.fireChannelRead(msg); //非心跳检测放行
	}

	private MiddleMsg buildHeatBeat(MiddleMsg msg) {
		MiddleMsg message = msg;
		message.getHeader().setTimestamp(new Date().getTime());
		message.getHeader().setStatus(200);
		message.getHeader().setType(MessageType.HEARTBEAT_RESP.value());
		return message;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.fireExceptionCaught(cause);
	}

}
