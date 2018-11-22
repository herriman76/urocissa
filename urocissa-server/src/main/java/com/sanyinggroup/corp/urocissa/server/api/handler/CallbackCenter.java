package com.sanyinggroup.corp.urocissa.server.api.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sanyinggroup.corp.urocissa.server.api.model.MiddleMsg;

/**
 * <p>Package:com.sanyinggroup.communication.server.api.handler</p> 
 * <p>Title:CallbackCenter</p> 
 * <p>Description: 推送回调中心</p> 
 * @author lixiao
 * @date 2017年10月18日 下午3:30:04
 * @version 
 * @since
 */
public class CallbackCenter {
	
	private static Map<String,Map<String,ServerPushCallback>> callbackMaps = new ConcurrentHashMap<String, Map<String,ServerPushCallback>>();
	//private static Map<String,ServerPushCallback> callbacks = new ConcurrentHashMap<String, ServerPushCallback>();
	/**
	 * <p>Title:addCallback</p> 
	 * <p>Description: 增加推送的回调函数</p> 
	 * @date 2017年10月18日 下午6:41:13
	 * @return Map<String,Map<String,ServerPushCallback>>
	 * @param sessionId
	 * @param key
	 * @param callback
	 * @return
	 * @since
	 */
	protected static Map<String,Map<String,ServerPushCallback>> addCallback(String sessionId,String key,ServerPushCallback callback){
		if(callbackMaps.get(sessionId)==null || callbackMaps.get(sessionId).size()==0){
			 Map<String,ServerPushCallback> callbacks = new ConcurrentHashMap<String, ServerPushCallback>();
			 callbacks.put(key, callback);
			 callbackMaps.put(sessionId, callbacks);
		}else{
			Map<String,ServerPushCallback> callbacks = callbackMaps.get(sessionId);
			if(callbacks.get(key)==null){ //Add if the key doesn't exist, otherwise it won't
				callbacks.put(key, callback);
			}
		}
		return callbackMaps;
	}
	
	protected static ServerPushCallback getCallback(String sessionId,String action){
		if(callbackMaps.get(sessionId)==null || callbackMaps.get(sessionId).size()==0){
			return null;
		}else{
			Map<String,ServerPushCallback> callbacks = callbackMaps.get(sessionId);
			return callbacks.get(action);
		}
	}
	protected static ServerPushCallback getCallback(MiddleMsg msg){
		if(msg==null|| msg.getHeader()==null || msg.getHeader().getSessionID()==null ||
				msg.getHeader().getAction()==null){
			return null;
		}
		String sessionId = msg.getHeader().getSessionID();
		String action = msg.getHeader().getAction();
		if(callbackMaps.get(sessionId)==null || callbackMaps.get(sessionId).size()==0){
			return null;
		}else{
			Map<String,ServerPushCallback> callbacks = callbackMaps.get(sessionId);
			return callbacks.get(action);
		}
	}
	
	protected static Map<String,Map<String,ServerPushCallback>> removeCallback(String sessionId,String action){
		if(null==sessionId ){
			callbackMaps.clear();
		}else {
			if(action==null){
				callbackMaps.remove(sessionId);
			}else{
				Map<String,ServerPushCallback> callbacks = callbackMaps.get(sessionId);
				callbacks.remove(action);
			}
		}
		return callbackMaps;
	}
	
} 
