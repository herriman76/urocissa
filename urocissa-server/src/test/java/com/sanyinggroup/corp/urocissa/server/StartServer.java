package com.sanyinggroup.corp.urocissa.server;

import java.util.ArrayList;
import java.util.List;

import io.netty.util.ResourceLeakDetector.Level;

import com.sanyinggroup.corp.urocissa.server.api.info.ClientApp;
import com.sanyinggroup.corp.urocissa.server.api.service.MsgServiceHandlerRegister;
import com.sanyinggroup.corp.urocissa.server.event.EventHandlerTest;
import com.sanyinggroup.corp.urocissa.server.test.common.Test1;
import com.sanyinggroup.corp.urocissa.server.test.common.Test2;

import io.netty.util.ResourceLeakDetector;

/**
 * demo
 */
public class StartServer {
	public static void main(String[] args) {
		try {
			ResourceLeakDetector.setLevel(Level.ADVANCED);
			//获取注册中心
			MsgServiceHandlerRegister register =MsgServiceHandlerRegister.getRegister();
			
			MsgServiceHandlerRegister.setEventHandlerClass(EventHandlerTest.class);
			//注册消息处理器
			register.addMsgServiceHandler("test1", Test1.class);
			//注册消息处理器
			register.addMsgServiceHandler("test2", Test2.class);
			List<ClientApp> appList = new ArrayList<ClientApp>();
			appList.add(new ClientApp("123","12345678"));
			
			System.out.println(register.getMsgServiceHandler("test2"));
			/*
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					while (true) {
						Map<String,Object> res = new HashMap<String, Object>();
						res.put("12312", 2343);
						res.put("object", new Object());
						res.put("list",new ArrayList<String>().add("aaa"));
						//服务端推送， 增加回执处理
						boolean bln = ServerPushHandler.pushByAppKey("123", "aaa", res,new ServerPushCallback() {
							
							@Override
							public void callback(MiddleMsg msg) {
								System.out.println("====推送后回执："+msg);
								
							}
						});
						System.out.println("++消息推送："+bln);
						try {
							Thread.sleep(15000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
			}).start();
			*/
			ServerInit.init(9166,appList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}


