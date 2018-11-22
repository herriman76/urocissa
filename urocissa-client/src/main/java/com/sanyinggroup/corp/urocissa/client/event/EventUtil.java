package com.sanyinggroup.corp.urocissa.client.event;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.client.init.Client;
import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.client.model.ResultObject;
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
	public  static void loginSuccess(ResultObject res,Class<?> eventHandlerClass) {
		if(eventHandlerClass != null && PARENTCLASS.isAssignableFrom(eventHandlerClass)) {
			try {
				Method loginSuccess = eventHandlerClass.getDeclaredMethod("loginSuccess", ResultObject.class);
				loginSuccess.invoke(eventHandlerClass.newInstance(), res);
			} catch (Exception e) {
				logger.error("登录成功事件通知失败:",e);
			}
		}else {
			logger.debug("没有注册登录成功后事件通知地址");
		}
	};
	public  static void loginSuccess(ResultObject res,Client client ,Class<?> eventHandlerClass) {
		if(eventHandlerClass != null && PARENTCLASS.isAssignableFrom(eventHandlerClass)) {
			try {
				Method loginSuccess = eventHandlerClass.getDeclaredMethod("loginSuccess", ResultObject.class,Client.class);
				loginSuccess.invoke(eventHandlerClass.newInstance(), res,client);
			} catch (Exception e) {
				logger.error("登录成功事件通知失败:",e);
			}
		}else {
			logger.debug("没有注册登录成功后事件通知地址");
		}
	};
	
	/**
	 * 登录失败
	 * @author lixiao create at 2018年1月12日 上午11:00:15 
	 * @since 2.0.0
	 */
	public  static void loginError(ResultObject res,Class<?> eventHandlerClass) {
		if(eventHandlerClass != null && PARENTCLASS.isAssignableFrom(eventHandlerClass)) {
			try {
				Method loginError = eventHandlerClass.getDeclaredMethod("loginError", ResultObject.class);
				loginError.invoke(eventHandlerClass.newInstance(), res);
			} catch (Exception e) {
				logger.error("登录失败事件通知失败:",e);
			}
		}else {
			logger.debug("没有注册登录失败后事件通知地址");
		}
		
	};
	/**
	 * 服务器断开连接
	 * @author lixiao create at 2018年1月12日 上午11:05:09 
	 * @since 2.0.0
	 * @param res
	 */
	public static void disconnected(ServerInfo res,Class<?> eventHandlerClass) {
		if(eventHandlerClass != null && PARENTCLASS.isAssignableFrom(eventHandlerClass)) {
			try {
				Method disconnected = eventHandlerClass.getDeclaredMethod("disconnected", ServerInfo.class);
				disconnected.invoke(eventHandlerClass.newInstance(), res);
			} catch (Exception e) {
				logger.error("服务器断开连接事件通知失败:",e);
			}
		}else {
			logger.debug("没有注册服务器断开连接后事件通知地址");
		}
		
	};
	/**
	 * 服务器连接失败
	 * @author lixiao create at 2018年1月16日 下午4:23:54 
	 * @since 1.0.0
	 * @param res
	 * @param eventHandlerClass
	 */
	public static void connectFail(ResultObject res,Class<?> eventHandlerClass) {
		if(eventHandlerClass != null && PARENTCLASS.isAssignableFrom(eventHandlerClass)) {
			try {
				Method connectFail = eventHandlerClass.getDeclaredMethod("connectFail", ResultObject.class);
				connectFail.invoke(eventHandlerClass.newInstance(), res);
			} catch (Exception e) {
				logger.error("服务器断开连接事件通知失败:",e);
			}
		}else {
			logger.debug("没有注册服务器断开连接后事件通知地址");
		}
		
	};
	/**
	 * 消息发送失败
	 * @author lixiao create at 2018年1月12日 上午11:04:51 
	 * @since 2.0.0
	 * @param msg
	 * @param res
	 */
	public static  void msgSendFail(MiddleMsg msg,ResultObject res,Class<?> eventHandlerClass) {
		if(eventHandlerClass != null && PARENTCLASS.isAssignableFrom(eventHandlerClass)) {
			try {
				Method msgSendFail = eventHandlerClass.getDeclaredMethod("msgSendFail",MiddleMsg.class ,ResultObject.class);
				msgSendFail.invoke(eventHandlerClass.newInstance(), msg,res);
			} catch (Exception e) {
				logger.error("消息发送失败事件通知失败:",e);
			}
		}else {
			logger.debug("没有注册消息发送失败后事件通知地址");
		}
	};
}
