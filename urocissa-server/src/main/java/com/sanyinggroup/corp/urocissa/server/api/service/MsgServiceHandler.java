package com.sanyinggroup.corp.urocissa.server.api.service;

import java.util.EventListener;

import com.sanyinggroup.corp.urocissa.server.api.model.MiddleMsg;



/**
 * <p>Package:com.sanyinggroup.communication.server.api.service</p> 
 * <p>Title:MsgServiceHandler</p> 
 * <p>Description: 消息处理器</p> 
 * @author lixiao
 * @date 2017年8月3日 下午3:51:35
 * @version
 */
public interface MsgServiceHandler extends EventListener {
	
	public MiddleMsg handleMsgEvent(MsgEvent dm, final MiddleMsg msg);
	
}
