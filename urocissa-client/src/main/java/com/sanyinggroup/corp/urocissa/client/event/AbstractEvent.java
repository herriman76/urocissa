package com.sanyinggroup.corp.urocissa.client.event;

import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.client.model.ResultObject;

/**
 * 事件驱动
 * @author lixiao create at 2018年1月12日 上午10:59:37 
 * @since 1.0.0
 */
public abstract class AbstractEvent {
	/**
	 * 登录成功
	 * @author lixiao create at 2018年1月12日 上午11:00:00 
	 * @since 1.0.0
	 */
	public abstract void loginSuccess();
	/**
	 * 登录失败
	 * @author lixiao create at 2018年1月12日 上午11:00:15 
	 * @since 1.0.0
	 */
	public abstract void loginError(ResultObject res);
	/**
	 * 服务器断开连接
	 * @author lixiao create at 2018年1月12日 上午11:05:09 
	 * @since 1.0.0
	 * @param res
	 */
	public abstract void disconnected(ResultObject res);
	/**
	 * 消息发送失败
	 * @author lixiao create at 2018年1月12日 上午11:04:51 
	 * @since 1.0.0
	 * @param msg
	 * @param res
	 */
	public abstract void msgSendFail(MiddleMsg msg,ResultObject res);
}
