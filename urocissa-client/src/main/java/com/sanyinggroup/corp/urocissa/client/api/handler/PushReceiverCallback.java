package com.sanyinggroup.corp.urocissa.client.api.handler;

import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
/**
 * 消息推送回调
 * @author lixiao create at 2017年7月10日 下午4:29:38 
 * @since 1.0.0
 */
public interface PushReceiverCallback extends PushReceiver{
	public MiddleMsg handleReceivedMsgWithCallback(MiddleMsg msg);
}
