package com.sanyinggroup.corp.urocissa.client.api.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sanyinggroup.corp.urocissa.client.codec.SecretManageCenter;


public class MsgHandlerCenter extends SecretManageCenter{
	private static Map<String,MsgHandler> handlers = new ConcurrentHashMap<String,MsgHandler>();
	
	protected static void regist(String key,MsgHandler handler) {
		handlers.put(key, handler);
	}
	
	
	protected static MsgHandler getMsgHandler(String key) {
		return handlers.get(key);
	}
}
