package com.sanyinggroup.corp.urocissa.server.api.handler;

import java.net.InetSocketAddress;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.core.model.AddressInfo;
import com.sanyinggroup.corp.urocissa.core.model.MessageType;
import com.sanyinggroup.corp.urocissa.core.util.SecretManagement;
import com.sanyinggroup.corp.urocissa.server.ServerGlobal;
import com.sanyinggroup.corp.urocissa.server.api.exception.BudMsgHeadException;
import com.sanyinggroup.corp.urocissa.server.api.exception.NoMsgServiceHandlerFound;
import com.sanyinggroup.corp.urocissa.server.api.info.ClientApp;
import com.sanyinggroup.corp.urocissa.server.api.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.server.api.service.MsgServerSource;
import com.sanyinggroup.corp.urocissa.server.api.service.MsgServiceHandlerRegister;
import com.sanyinggroup.corp.urocissa.server.event.EventInfo;
import com.sanyinggroup.corp.urocissa.server.event.EventUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * <p>
 * Package:com.sanyinggroup.communication.server.api.handler
 * </p>
 * <p>
 * Title:ServiceHandler
 * </p>
 * <p>
 * Description: 服务端消息处理
 * </p>
 * 
 * @author lixiao
 * @date 2017年7月14日 下午2:09:41
 * @version
 */
public class ServiceHandler extends SimpleChannelInboundHandler<MiddleMsg> {
	private static final Logger logger = LoggerFactory.getLogger(ServiceHandler.class);
	// private static volatile long receiveCount=0l;
	private MiddleMsg err; // since v1.0 当服务器发生异常时，将code500返给客户端

	/**
	 * 消息处理，回调各业务层消息处理并返回处理结果
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, MiddleMsg msg) {
		err = msg;
		long n = ServerGlobal.receiveMsg(msg.getHeader().getSessionID());
		logger.debug(
				"\n -----------------------------------------------------\n" + "收到来自：" + ctx.channel().remoteAddress()
						+ "第  " + n + " 的消息 : " + msg + "\n------------------------------------------------");
		if (msg.getHeader().getStatus() < 0) { // 解析或签名已经不被允许了
			ctx.writeAndFlush(msg);
			logger.warn("解析或签名已经不被允许了,原样返回："+msg);
			return;
		}
		MiddleMsg ori = msg;
		MsgServerSource source = new MsgServerSource(msg);
		MiddleMsg res = null;
		try {
			// res = source.notifyMsgServiceHandler(msg);
			// @since 1.0.1
			InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
			InetSocketAddress localAddress = (InetSocketAddress) ctx.channel().localAddress();
			AddressInfo addressInfo = new AddressInfo(remoteAddress, localAddress);
			res = source.notifyMsgServiceHandler(addressInfo, msg);
			// end since 1.0.1
			if (res == null) {
				res = ori;
				res.getMsgHead().setStatus(0);
				res.getHeader().getAttachment().put("statusMsg", "服务器处理结果为空");
				res.setBody(null);
				logger.warn("服务器处理结果为空");
			} else {
				//since 1.0.3 推送回调不做处理
				if(res.getHeader()!=null && res.getHeader().getType() == MessageType.CLIENT_RESP_FOR_SERVICE_PUSH.value()){
					
				}else{
					res.setHeader(ori.getHeader());
					res.getMsgHead().setStatus(200);
					res.getHeader().getAttachment().put("statusMsg", "success");
				}
			}
		} catch (BudMsgHeadException e) {
			res = ori;
			res.getMsgHead().setStatus(400);
			res.getHeader().getAttachment().put("statusMsg", "请求消息头有误");
			res.setBody(null);
			logger.error("请求消息头有误", e);
		} catch (NoMsgServiceHandlerFound e) {
			res = ori;
			res.getMsgHead().setStatus(404);
			res.getHeader().getAttachment().put("statusMsg", "未找到相应的action：404");
			res.setBody(null);
			logger.error("未找到相应的action：404", e);
		} catch (Exception e) {
			res = ori;
			res.getMsgHead().setStatus(500);
			res.getHeader().getAttachment().put("statusMsg", "服务端消息处理异常");
			res.setBody(null);
			logger.error("服务端消息处理异常", e);
		}
		Object obj = msg.getHeader().getAttachment().get("v02handerkey");
		if (obj != null) {
			res.getHeader().getAttachment().put("v02handerkey", obj);
		}
		// res.getHeader().setTimestamp(new Date().getTime());
		//since 1.0.3 推送回调不做处理
		if(res.getHeader()!=null && res.getHeader().getType() == MessageType.CLIENT_RESP_FOR_SERVICE_PUSH.value()){
			ServerPushCallback callback = CallbackCenter.getCallback(res);
			if(callback!=null){
				callback.callback(res);
			}
		}else{
			res.getMsgHead().setType(MessageType.SERVICE_RESP.value());
			ctx.writeAndFlush(res);
		}

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		removeClient(ctx);
		logger.debug("客户端断开连接");
	}
	private void removeClient(ChannelHandlerContext ctx) {
		try {
			if(ctx==null || ctx.channel() ==null || ctx.channel().id()==null) {
				logger.warn("未找到需要移除的客户端，或已移除");
				return;	
			}
			String sessionid = ServerGlobal.channelId2SessionId.get(ctx.channel().id().asShortText());
			if (StringUtils.isNotBlank(sessionid)) {
				SecretManagement man = ServerGlobal.sessionWithAppKeys.get(sessionid);
				if(man==null ||man.getAppKey()==null) {
					ClientApp appinfo = new ClientApp();
					appinfo.setSessionId(sessionid);
					EventInfo info = new EventInfo(-200, "客户端下线", appinfo);
					EventUtil.disconnected(info, MsgServiceHandlerRegister.getEventHandlerClass());
					logger.warn("SecretManagement=null,未找到需要移除的客户端，或已离线");
					ServerGlobal.sessionWithAppKeys.remove(sessionid);
					ServerGlobal.clientDisConnected();
					return;
				}
				//客户端连接成功设置事件驱动回调
				ClientApp appinfo = new ClientApp(man.getAppKey(), man.getAppSecret());
				appinfo.setIp(man.getIp() + ":" + man.getPort());
				appinfo.setSessionId(sessionid);
				appinfo.setChannelId(ctx.channel().id().asShortText());
				EventInfo info = new EventInfo(-200, "客户端下线", appinfo);
				EventUtil.disconnected(info, MsgServiceHandlerRegister.getEventHandlerClass());
				//---------回调结束--------------
				logger.debug(appinfo.getIp() + ",appkey=" + appinfo.getAppKey() + " 下线");
				ServerGlobal.sessionWithAppKeys.remove(sessionid);
				ServerGlobal.clientDisConnected();
			} 
		} catch (Exception e) {
			logger.error("移除客户端异常",e);
		}
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		
		try {
			if (cause instanceof io.netty.handler.timeout.ReadTimeoutException) {
				logger.debug("长时间没有读写----会话关闭");
			} else if ("远程主机强迫关闭了一个现有的连接。".equals(cause.getMessage())) {
				logger.error("远程主机强迫关闭了一个现有的连接。");
				
			} else {
				logger.error("消息处理异常", cause);
				ServerGlobal.handleFalse();
				if (err != null) {
					err.getMsgHead().setStatus(500);
					err.getHeader().getAttachment().put("statusMsg", "服务端消息处理异常");
					err.setBody(null);
					ctx.writeAndFlush(err);
				}
			}
		} catch (Exception e) {
			logger.error("异常捕捉后异常",e);
		}finally {
			//removeClient(ctx) ;
			// 发生异常关闭通道，发送消息时会自动重连，还有个功能就是非法连接，直接关闭通道
			ctx.close();
		}
	}

}
