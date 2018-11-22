package com.sanyinggroup.corp.urocissa.server.api.handler;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.core.model.MessageType;
import com.sanyinggroup.corp.urocissa.core.util.SecretManagement;
import com.sanyinggroup.corp.urocissa.server.ServerGlobal;
import com.sanyinggroup.corp.urocissa.server.api.model.MiddleMsg;


/**
 * <p>Package:com.sanyinggroup.communication.server.api.handler</p> 
 * <p>Title:ServerPushHandler</p> 
 * <p>Description: 服务器推送类</p> 
 * @author lixiao
 * @date 2017年10月12日 下午4:11:30
 * @version 
 * @since 1.0.2
 */
public class ServerPushHandler {
	private static final Logger logger = LoggerFactory.getLogger(ServerPushHandler.class);
	/**
	 * <p>Title:pushByAppKey</p> 
	 * <p>Description: 根据appkey或者所有的推送</p> 
	 * @date 2017年10月13日 上午10:30:50
	 * @return boolean
	 * @param appkey
	 * @param action
	 * @param MessageBody
	 * @param isAllowAppkeyIsNull
	 * @return
	 * @since
	 */
	private static boolean pushByAppKey(String appkey,String action,Object MessageBody,boolean isAllowAppkeyIsNull,ServerPushCallback callback){
		boolean bln = false;
		action  = nullToBlank(action);
		Map<String, SecretManagement> sessionWithAppKeys  = ServerGlobal.sessionWithAppKeys;
		MiddleMsg msg = new MiddleMsg(action, MessageType.SERVICE_PUSH.value(), MessageBody);
		Set<String> sessionids = sessionWithAppKeys.keySet();
		
		//appkey 为空的情况 推送所有机器
		if(appkey!=null && !("").equals(appkey)){
			for(String key:sessionids){
				SecretManagement m = sessionWithAppKeys.get(key);
				if(m!=null && appkey.equals(m.getAppKey()) && m.getChannel()!=null && m.getChannel().isWritable()){
					msg.getMsgHead().setAppKey(m.getAppKey());
					msg.getMsgHead().setSessionID(key);
					msg.getMsgHead().setStatus(200);
					if(callback!=null){//注册回调
						msg.getHeader().getAttachment().put("_serverPushNeedCallback", "1");
						CallbackCenter.addCallback(key, action, callback);
					}
					m.getChannel().writeAndFlush(msg);
					bln = true;
				}
			}
		}else{
			if(isAllowAppkeyIsNull){
				for(String key:sessionids){
					SecretManagement m = sessionWithAppKeys.get(key);
					if(m!=null  && m.getChannel()!=null && m.getChannel().isWritable()){
						msg.getMsgHead().setSessionID(key);
						msg.getMsgHead().setAppKey(m.getAppKey());
						msg.getMsgHead().setStatus(200);
						if(callback!=null){//注册回调
							msg.getHeader().getAttachment().put("_serverPushNeedCallback", "1");
							CallbackCenter.addCallback(key, action, callback);
						}
						m.getChannel().writeAndFlush(msg);
						bln = true;
						logger.debug("--->->->服务器推送消息："+msg);
					}
				}
			}else{
				logger.error("pushByAppKey:appkey不能为空");
				return false;
			}
		}
		return bln;
	}
	/**
	 * <p>Title:pushByAppKey</p> 
	 * <p>Description: 根据appkey推送消息指定客户端</p> 
	 * @date 2017年10月13日 上午10:32:07
	 * @return boolean
	 * @param appkey
	 * @param action 接收端名称
	 * @param MessageBody 发送的消息体
	 * @return
	 * @since
	 */
	public static boolean pushByAppKey(String appkey,String action,Object MessageBody){
		return pushByAppKey(appkey, action, MessageBody, false,null);
	}
	
	/**
	 * <p>Title:push</p> 
	 * <p>Description: 消息推送所有客户端</p> 
	 * @date 2017年10月12日 下午6:12:58
	 * @return boolean
	 * @param MessageBody 发送的消息体
	 * @since
	 */
	public static boolean pushAll(Object MessageBody){
		return pushByAppKey(null, null, MessageBody,true,null);
	}
	/**
	 * <p>Title:pushAll</p> 
	 * <p>Description: </p> 
	 * @date 2017年10月13日 上午10:55:13
	 * @return boolean
	 * @param action 根据action名称发送推送
	 * @param MessageBody
	 * @return
	 * @since
	 */
	public static boolean pushAll(String action,Object MessageBody){
		return pushByAppKey(null, action, MessageBody,true,null);
	}
	
	/**
	 * <p>Title:pushAll</p> 
	 * <p>Description: </p> 
	 * @date 2017年10月13日 上午10:55:13
	 * @return boolean
	 * @param action 根据action名称发送推送
	 * @param MessageBody
	 * @param callback
	 * @return
	 * @since
	 */
	public static boolean pushAll(String action,Object MessageBody,ServerPushCallback callback){
		return pushByAppKey(null, action, MessageBody,true,callback);
	}
	/**
	 * <p>Title:pushBySessionId</p> 
	 * <p>Description: 根据sessionid推送指定客户端</p> 
	 * @date 2017年10月13日 上午10:35:42
	 * @return boolean
	 * @param appkey
	 * @param action 接收端名称
	 * @param MessageBody 发送的消息体
	 * @return
	 * @since
	 */
	public static boolean pushBySessionId(String sessionid,String action,Object MessageBody,ServerPushCallback callback){
		boolean bln = false;
		action  = nullToBlank(action);
		MiddleMsg msg = new MiddleMsg(action, MessageType.SERVICE_PUSH.value(), MessageBody);
		SecretManagement m = ServerGlobal.sessionWithAppKeys.get(sessionid);
		if(m!=null  && m.getChannel()!=null && m.getChannel().isWritable()){
			msg.getMsgHead().setSessionID(sessionid);
			msg.getMsgHead().setAppKey(m.getAppKey());
			msg.getMsgHead().setStatus(200);
			if(callback!=null){ //注册回调
				msg.getHeader().getAttachment().put("_serverPushNeedCallback", "1");
				CallbackCenter.addCallback(sessionid, action, callback);
			}
			m.getChannel().writeAndFlush(msg);
			bln = true;
			logger.debug("--->->->服务器推送消息："+msg);
		}
		return bln;
	}
	/**
	 * <p>Title:pushByAppKey</p> 
	 * <p>Description: 根据appkey推送消息指定客户端</p> 
	 * @date 2017年10月19日 上午9:55:00
	 * @return boolean
	 * @param appkey
	 * @param action 接收端名称
	 * @param MessageBody 发送的消息体
	 * @param callback 回调函数
	 * @since 1.0.3
	 */
	public static boolean pushByAppKey(String appkey,String action,Object MessageBody,ServerPushCallback callback){
		return pushByAppKey(appkey, action, MessageBody, false,callback);
	}
	
	private static String nullToBlank(String str){
		if(str==null){
			return "";
		}else{
			return str;
		}
	}
	
}
