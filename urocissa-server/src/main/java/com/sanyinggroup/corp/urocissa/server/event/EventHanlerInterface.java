package com.sanyinggroup.corp.urocissa.server.event;


/**
 * 事件接口
 * @author lixiao create at 2018年1月12日 上午11:16:41 
 * @since 1.0.0
 */
public interface EventHanlerInterface {
	
	public  void loginSuccess(EventInfo res);
	/**
	 * 登录失败
	 * @author lixiao create at 2018年1月12日 上午11:00:15 
	 * @since 1.0.0
	 */
	//public  void loginError(EventInfo res);
	/**
	 * 服务器断开连接
	 * @author lixiao create at 2018年1月12日 上午11:05:09 
	 * @since 2.0.0
	 * @param res
	 */
	public void disconnected(EventInfo res);
	
	
	void  serverClose();
	/**
	 * 连接服务器失败
	 * @author lixiao create at 2018年1月12日 上午11:05:09 
	 * @since 2.0.0
	 * @param res
	 */
	//public void connectFail(EventInfo res);
	
	/**
	 * 消息发送失败
	 * @author lixiao create at 2018年1月12日 上午11:04:51 
	 * @since 2.0.0
	 * @param msg
	 * @param res
	 */
	//public  void msgSendFail(MiddleMsg msg,EventInfo res);
}
