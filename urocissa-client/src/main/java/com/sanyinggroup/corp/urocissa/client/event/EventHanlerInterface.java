package com.sanyinggroup.corp.urocissa.client.event;

import com.sanyinggroup.corp.urocissa.client.init.Client;
import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.client.model.ResultObject;
/**
 * 事件接口
 * @author lixiao create at 2018年1月12日 上午11:16:41 
 * @since 1.0.0
 */
public interface EventHanlerInterface {
	
	public  void loginSuccess(ResultObject res);
	/**
	 * 登录成功
	 * @author lixiao create at 2018年5月7日 下午4:50:05 
	 * @since 1.0.0
	 * @param res
	 * @param client
	 */
	public void loginSuccess(ResultObject res,Client client);
	/**
	 * 登录失败
	 * @author lixiao create at 2018年1月12日 上午11:00:15 
	 * @since 2.0.0
	 */
	public  void loginError(ResultObject res);
	/**
	 * 服务器断开连接
	 * @author lixiao create at 2018年1月12日 上午11:05:09 
	 * @since 2.0.0
	 * @param res
	 */
	public void disconnected(ServerInfo res);
	/**
	 * 连接服务器失败
	 * @author lixiao create at 2018年1月12日 上午11:05:09 
	 * @since 2.0.0
	 * @param res
	 */
	public void connectFail(ResultObject res);
	
	/**
	 * 消息发送失败
	 * @author lixiao create at 2018年1月12日 上午11:04:51 
	 * @since 2.0.0
	 * @param msg
	 * @param res
	 */
	public  void msgSendFail(MiddleMsg msg,ResultObject res);
}
