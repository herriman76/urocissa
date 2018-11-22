package com.sanyinggroup.corp.urocissa.server.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 事件驱动
 * @author lixiao create at 2018年1月12日 上午10:59:37 
 * @since 2.0.0
 */
public abstract class AbstractEventHandler implements EventHanlerInterface{
	private static final Logger logger  = LoggerFactory.getLogger(AbstractEventHandler.class);
	/**
	 * 登录成功
	 * @author lixiao create at 2018年1月12日 上午11:00:00 
	 * @since 2.0.0
	 */
	public  void  loginSuccess(EventInfo res) {
		logger.info("登录成功后事件回调"+res);
	}
	/**
	 * 登录失败
	 * @author lixiao create at 2018年1月12日 上午11:00:15 
	 * @since 2.0.0
	 *//*
	public void loginError(EventInfo res) {
		logger.error("登录失败后事件回调"+res);
	}*/
	/**
	 * 服务器断开连接
	 * @author lixiao create at 2018年1月12日 上午11:05:09 
	 * @since 2.0.0
	 * @param res
	 */
	public void disconnected(EventInfo res){
		logger.error("服务器断开连接"+res);
	}
	/**
	 * 连接服务器失败
	 * @author lixiao create at 2018年1月12日 上午11:05:09 
	 * @since 2.0.0
	 * @param res
	 */
	/*public void connectFail(EventInfo res){
		logger.error("连接服务器失败"+res);
	}*/
	/**
	 * 消息发送失败
	 * @author lixiao create at 2018年1月12日 上午11:04:51 
	 * @since 2.0.0
	 * @param msg
	 * @param res
	 */
	/*public void msgSendFail(MiddleMsg msg,EventInfo res) {
		logger.error("消息发送失败后事件回调"+res);
	}*/
}
