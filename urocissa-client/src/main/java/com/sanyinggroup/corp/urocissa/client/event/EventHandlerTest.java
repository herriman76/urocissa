package com.sanyinggroup.corp.urocissa.client.event;


import com.sanyinggroup.corp.urocissa.client.init.Client;
import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.client.model.ResultObject;
/**
 * 事件处理 demo 类
 * @author lixiao create at 2018年1月16日 下午3:25:39 
 * @since 2.0.0
 */
public class EventHandlerTest extends AbstractEventHandler{
	@Override
	public void loginSuccess(ResultObject res) {
		super.loginSuccess(res);
		System.out.println("=========================");
	}

	@Override
	public void loginError(ResultObject res) {
		super.loginError(res);
		System.out.println("=========================");
	}

	@Override
	public void disconnected(ServerInfo res) {
		super.disconnected(res);
		System.out.println("=========================");
		
	}

	@Override
	public void msgSendFail(MiddleMsg msg, ResultObject res) {
		super.msgSendFail(msg,res);
		System.out.println("=========================");
		
	}
	
	/**
	 * 连接服务器失败
	 * @author lixiao create at 2018年1月12日 上午11:05:09 
	 * @since 2.0.0
	 * @param res
	 */
	public void connectFail(ResultObject res){
		super.connectFail(res);
		System.out.println("=========================");
	}

	@Override
	public void loginSuccess(ResultObject res, Client client) {
		System.out.println("=========================");
		System.out.println(client);
		
	}

	
}
