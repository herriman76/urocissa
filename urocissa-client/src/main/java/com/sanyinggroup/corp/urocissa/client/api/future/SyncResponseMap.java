package com.sanyinggroup.corp.urocissa.client.api.future;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;

/**
 * 
 * <p>Package:com.sanyinggroup.corp.urocissa.client.api.future</p> 
 * <p>Title:SyncWriteMap</p> 
 * <p>Description: 同步的消息结果</p> 
 * @author lixiao
 * @date 2017年8月16日 下午5:36:46
 * @since 1.0.0
 */
public class SyncResponseMap {
	    public volatile static Map<String, SendFuture<MiddleMsg>> syncKey = new ConcurrentHashMap<String, SendFuture<MiddleMsg>>();

}
