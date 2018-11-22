package com.sanyinggroup.corp.urocissa.core.model;
/**
 * 
 * <p>Package:com.sanyinggroup.communication.server.api.msg</p> 
 * <p>Title:MessageType</p> 
 * <p>Description: 消息类型 </p> 
 * @author lixiao
 * @date 2017年7月19日 下午1:45:03
 * @version
 */
public enum MessageType {
	CLIENT_REQ((byte) 0),  //客户端请求 request
	SERVICE_RESP((byte) 1), // response
	ONE_WAY((byte) 2),  // 单线路
	LOGIN_REQ((byte) 3), // 登录请求
	LOGIN_RESP((byte) 4), // 登录响应
	HEARTBEAT_REQ((byte) 5), // 心跳类型
	HEARTBEAT_RESP((byte) 6), // 心跳响应
	SERVICE_PUSH((byte) 7), //服务端推送
	CLIENT_RESP_FOR_SERVICE_PUSH((byte) 8); //客户端的服务器回调
	private byte value;

	private MessageType(byte value) {
		this.value = value;
	}

	public byte value() {
		return this.value;
	}
}
