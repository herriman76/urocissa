package com.sanyinggroup.corp.urocissa.client.api.handler;

import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
/**
 * 
 * <p>Package:com.sanyinggroup.corp.urocissa.client.api.handler</p> 
 * <p>Title:MsgHandler</p> 
 * <p>Description: 客户端 收到消息回复后的回调处理 </p> 
 * <p>
 * 消息回调处理器
 * 每发送一个异步消息的，都会对应一个消息回调处理器，即：服务端处理消息返回后，由此处理器去处理服务端返回的消息
 * 注意： 每个服务端地址的action对应一个handler，即handler初始化后，后期往服务端相同的action上发送消息时，会使用初始化的handler进行消息处理
 * handler注册代码如下：
 * <code>
 * String handlerKey = MD5.toMD5(config.getIp()+config.getPort()+config.getAppKey()+msg.getHeader().getAction());
 *				if(MsgHandlerCenter.getMsgHandler(handlerKey)==null){ //判断handler注册中心是否已经注册
 *					MsgHandlerCenter.regist(handlerKey, handler); //handler注册到注册中心
 *				}
 * </code>
 * </p>
 * @author lixiao
 * @date 2017年7月18日 下午3:22:33
 * @version
 */
public interface MsgHandler {
	
	public void callback(MiddleMsg msg);
	
}
