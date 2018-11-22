package com.sanyinggroup.corp.urocissa.server.event;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事件回调工具类
 * @author lixiao create at 2018年1月16日 下午3:06:41 
 * @since 2.0.0
 */
public class EventUtil {
	private static final Logger logger = LoggerFactory.getLogger(EventUtil.class);
	private static final Class<AbstractEventHandler> PARENTCLASS = AbstractEventHandler.class;
	/**
	 * 登录成功事件通知
	 * @author lixiao create at 2018年1月16日 下午2:48:43 
	 * @since 2.0.0
	 * @param res
	 * @param eventHandlerClass
	 */
	public  static void loginSuccess(EventInfo res,Class<?> eventHandlerClass) {
		if(eventHandlerClass != null && PARENTCLASS.isAssignableFrom(eventHandlerClass)) {
			try {
				Method loginSuccess = eventHandlerClass.getDeclaredMethod("loginSuccess", EventInfo.class);
				loginSuccess.invoke(eventHandlerClass.newInstance(), res);
			} catch (Exception e) {
				logger.error("登录成功事件通知失败:",e);
			}
		}else {
			logger.debug("没有注册登录成功后事件通知地址");
		}
	};
	/**
	 * 服务端退出
	 * @author lixiao create at 2018年1月12日 上午11:00:15 
	 * @since 2.0.0
	 */
	public  static void serverClose(Class<?> eventHandlerClass) {
		if(eventHandlerClass != null && PARENTCLASS.isAssignableFrom(eventHandlerClass)) {
			try {
				Method serverClose = eventHandlerClass.getDeclaredMethod("serverClose");
				serverClose.invoke(eventHandlerClass.newInstance());
			} catch (Exception e) {
				logger.error("服务端退出失败:",e);
			}
		}else {
			logger.debug("服务端退出时间通知地址");
		}
		
	};
	/**
	 * 服务器断开连接
	 * @author lixiao create at 2018年1月12日 上午11:05:09 
	 * @since 2.0.0
	 * @param res
	 */
	public static void disconnected(EventInfo res,Class<?> eventHandlerClass) {
		if(eventHandlerClass != null && PARENTCLASS.isAssignableFrom(eventHandlerClass)) {
			try {
				Method disconnected = eventHandlerClass.getDeclaredMethod("disconnected", EventInfo.class);
				disconnected.invoke(eventHandlerClass.newInstance(), res);
			} catch (Exception e) {
				logger.error("服务器断开连接事件通知失败:",e);
			}
		}else {
			logger.debug("没有注册服务器断开连接后事件通知地址");
		}
		
	}
	
	
	
	/**
	 * 消息发送失败
	 * @author lixiao create at 2018年1月12日 上午11:04:51 
	 * @since 2.0.0
	 * @param msg
	 * @param res
	 */
	/*
	public static  void msgSendFail(MiddleMsg msg,EventInfo res,Class<?> eventHandlerClass) {
		if(eventHandlerClass != null && PARENTCLASS.isAssignableFrom(eventHandlerClass)) {
			try {
				Method msgSendFail = eventHandlerClass.getDeclaredMethod("msgSendFail",MiddleMsg.class ,EventInfo.class);
				msgSendFail.invoke(eventHandlerClass.newInstance(), msg,res);
			} catch (Exception e) {
				logger.error("消息发送失败事件通知失败:",e);
			}
		}else {
			logger.debug("没有注册消息发送失败后事件通知地址");
		}
	};*/
}
