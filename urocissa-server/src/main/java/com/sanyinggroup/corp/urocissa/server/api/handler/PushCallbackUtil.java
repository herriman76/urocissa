package com.sanyinggroup.corp.urocissa.server.api.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 推送回调工具类
 * @author lixiao create at 2017年7月10日 下午2:48:24 
 * @since 1.0.0
 */
public class PushCallbackUtil {
	public Map<String,ServerPushCallback> callbacks = new ConcurrentHashMap<String, ServerPushCallback>();
	
	
}
