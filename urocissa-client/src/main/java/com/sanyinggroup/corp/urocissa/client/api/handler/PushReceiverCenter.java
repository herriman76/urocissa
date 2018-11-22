package com.sanyinggroup.corp.urocissa.client.api.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
/**
 * 消息回调中心
 * @author lixiao create at 2017年8月10日 下午4:30:10 
 * @since 1.0.0
 */
public class PushReceiverCenter {
	private static Map<String,PushReceiver> receivers = new ConcurrentHashMap<String, PushReceiver>(1);
	/**
	 * 注册 推送接收处理器
	 * @author lixiao create at 2018年1月18日 上午11:33:10 
	 * @since 1.0.0
	 * @param actionName
	 * @param receiver
	 * @return
	 */
	public static boolean  registReceiver(String actionName,PushReceiver receiver){
		if(actionName==null || ("").equals(actionName)){
			actionName = "";
		}
		receivers.put(actionName, receiver);
		return true;
	}
	/**
	 * 移除 推送接收处理器
	 * @author lixiao create at 2018年1月18日 上午11:33:35 
	 * @since 1.0.0
	 * @param actionName
	 * @return
	 */
	public static boolean  removeReceiver(String actionName){
		if(actionName==null || ("").equals(actionName)){
			actionName = "";
		}
		receivers.remove(actionName);
		return true;
	}
	protected static PushReceiver getReceiver(String actionName){
		if(actionName==null || ("").equals(actionName)){
			actionName = "";
		}
		return receivers.get(actionName);
	}
	protected static PushReceiver getReceiver(MiddleMsg msg){
		if(msg!=null && msg.getHeader()!=null){
			return getReceiver(msg.getMsgHead().getAction());
		}
		return null;
	}
}
