package com.sanyinggroup.corp.urocissa.server.api.handler;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.core.model.MessageType;
import com.sanyinggroup.corp.urocissa.core.util.SecretManagement;
import com.sanyinggroup.corp.urocissa.server.ServerConfig;
import com.sanyinggroup.corp.urocissa.server.ServerGlobal;
import com.sanyinggroup.corp.urocissa.server.api.info.ClientApp;
import com.sanyinggroup.corp.urocissa.server.api.model.ClientDeviceInfo;
import com.sanyinggroup.corp.urocissa.server.api.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.server.api.model.MsgHead;
import com.sanyinggroup.corp.urocissa.server.api.service.MsgServiceHandlerRegister;
import com.sanyinggroup.corp.urocissa.server.event.EventInfo;
import com.sanyinggroup.corp.urocissa.server.event.EventUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.sf.json.JSONObject;
/**
 * <p>Package:com.sanyinggroup.communication.server.api.handler</p> 
 * <p>Title:LoginAuthRespHandler</p> 
 * <p>Description: 登录请求认证</p> 
 * <p>首先进行appkey认证，认证通过会会按照认证策略进行ip认证</p>
 * <p>每个登录认证成功后返回一个sessionId</p>
 * <p>消息处理过程中将进行sessionId验证</p>
 * @author lixiao
 * @date 2017年7月14日 上午10:50:00
 * @version
 */
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter  {
	private static final Logger logger =LoggerFactory.getLogger(LoginAuthRespHandler.class);
	
	//public static Map<String, String> appKeys = new ConcurrentHashMap<String, String>();
	/**
	 * 连接认证
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		MiddleMsg message = (MiddleMsg) msg;
		// 如果是握手请求消息，处理，其它消息透传
		MiddleMsg loginResp = null;
		InetSocketAddress address = (InetSocketAddress) ctx.channel()
				.remoteAddress();
		String ip = address.getAddress().getHostAddress();
		int port =  address.getPort();
		if (message.getMsgHead() == null) {
			logger.info("非法连接"+ip);
			loginResp  =buildResponse(message,"","非法连接");
			loginResp.getMsgHead().setStatus(-120);
			ctx.writeAndFlush(loginResp);
			ctx.close();
		}else if(!ServerConfig.checkIp(ip)){//ip检查的不通过
			loginResp = buildResponse(message,"","非法连接,IP不允许");
			logger.info("非法连接,IP不允许:"+ip);
			loginResp.getHeader().setStatus(-102);
			ctx.writeAndFlush(loginResp);
			ctx.close();
		}else if(!ServerConfig.checkAppKey(message.getMsgHead().getAppKey())){ // 检测app是否合法
				loginResp =  buildResponse(message,"","非法连接,appKey不允许");
				logger.error(address.getHostName()+"非法连接,appKey不允许");
				loginResp.getHeader().setStatus(-101);
				ctx.writeAndFlush(loginResp);
				ctx.close();
		}else {
			String appkey = message.getMsgHead().getAppKey();
			if(message.getHeader().getType() == MessageType.LOGIN_REQ.value() ){ //登录请求
				if(!ServerConfig.loginCheckSign(appkey, message)){ //签名不正确
					loginResp =  buildResponse(message,"","非法连接,签名错误，请检查appSecret");
					logger.debug(message.toString());
					logger.error(appkey+" 非法连接,签名错误，可能appsercet错误");
					loginResp.getHeader().setStatus(-103);
					ctx.writeAndFlush(loginResp);
					ctx.close();
				}else{
					try {
						String sessionid = getSessionId();
						SecretManagement man = new SecretManagement(appkey, ServerConfig.getAppKeys().get(appkey).getAppSecret());
						man.setIp(ip);
						man.setPort(port);
						man.setTimestamp(message.getMsgHead().getTimestamp());
						man.setChannel(ctx.channel()); //保存channel
						ServerGlobal.sessionWithAppKeys.put(sessionid, man ); //登录成功，初始加密秘钥
						ServerGlobal.channelId2SessionId.put(ctx.channel().id().asShortText(), sessionid); //设置channelid和sessionid关联
						ServerGlobal.clientConnected();
						loginResp = buildResponse(message,sessionid,"登录成功");
						loginResp.getHeader().setStatus(200);
						loginResp.setBody(man.getNextSecret());
						//客户端连接成功设置事件驱动回调
						ClientApp appinfo =  new ClientApp(man.getAppKey(), man.getAppSecret());
						appinfo.setIp(ip+":"+port);
						appinfo.setSessionId(loginResp.getHeader().getSessionID());
						appinfo.setChannelId(ctx.channel().id().asShortText());
						if(null != message.getHeader().getAttachment().get("deviceInfo")) {
							JSONObject fromObject = JSONObject.fromObject(message.getHeader().getAttachment().get("deviceInfo"));
							ClientDeviceInfo deviceInfo = (ClientDeviceInfo) JSONObject.toBean(fromObject, ClientDeviceInfo.class);
							appinfo.setDeviceInfo(deviceInfo);
						}
						//appinfo.setDeviceId();
						EventInfo info = new EventInfo(200, "登录成功", appinfo);
						EventUtil.loginSuccess(info, MsgServiceHandlerRegister.getEventHandlerClass());
					} catch (Exception e) {
						logger.error("====客户端登录连接失败======",e);
					}
					logger.debug("====客户端登录连接成功======");
					
				}
				ctx.writeAndFlush(loginResp);
			}else if(message.getHeader().getType() == MessageType.CLIENT_REQ.value() || 
						message.getHeader().getType() ==MessageType.HEARTBEAT_REQ.value()
						|| message.getHeader().getType() ==MessageType.CLIENT_RESP_FOR_SERVICE_PUSH.value()){
				logger.debug("sessionId:"+message.getMsgHead().getSessionID());
				if(message.getHeader().getStatus()<0){ //签名或密码错误
					ctx.fireChannelRead(msg);
					//ctx.close();
					return ;
				}
				if(ServerGlobal.sessionWithAppKeys.get(message.getMsgHead().getSessionID())!=null){
					//已经登录过了，消息传递，下一步处理
					ctx.fireChannelRead(msg);
				}else{
					logger.info("未检出到登录信息");
					loginResp  = buildResponse(message,"","未检出到登录信息");
					loginResp.getMsgHead().setStatus(-110);
					ctx.writeAndFlush(loginResp);
					ctx.close();
				}
			}else{
				logger.info(ip+"客户端请求类型有误："+message.getHeader().getType());
				loginResp  = buildResponse(message,"","请求类型有误");
				loginResp.getMsgHead().setStatus(-111);
				ctx.writeAndFlush(loginResp);
				ctx.close();
			}
		
		}
	}
	
	/*private MiddleMsg buildResponse(byte result) {
		MiddleMsg message = new MiddleMsg();
		MsgHead header = new MsgHead();
		header.setType(MessageType.LOGIN_RESP.value());
		message.setHeader(header);
		message.setBody(result);
		return message;
	}*/
	private MiddleMsg buildResponse(MiddleMsg msg,String result) {
		return buildResponse(msg,"",result);
	}
	@SuppressWarnings("unused")
	private MiddleMsg buildResponse(String result) {
		return buildResponse(null,result);
	}
	private MiddleMsg buildResponse(MiddleMsg reqMsg,String sessionID,Object result) {
		MiddleMsg msg = reqMsg;
		if(msg == null){
			msg = new MiddleMsg();
			MsgHead header = new MsgHead();
			header.setType(MessageType.LOGIN_RESP.value());
			msg.setHeader(header);
			msg.getHeader().setTimestamp(new Date().getTime());
		}
		msg.getHeader().setType(MessageType.LOGIN_RESP.value());
		msg.getHeader().setSessionID(sessionID);
		msg.getHeader().setTimestamp(new Date().getTime());
		msg.getHeader().getAttachment().put("statusMsg", result);
		msg.setBody(null); //将返回值设置为null
		return msg;
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.fireExceptionCaught(cause);
	}
	
	public  String getSessionId() {
	    try {
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        md.update((new Date().getTime()+"").getBytes());
	        return new BigInteger(1, md.digest()).toString(16);
	    } catch (Exception e) {
	       e.printStackTrace();
	    }
	    return new Date().getTime()+"";
	}

}
