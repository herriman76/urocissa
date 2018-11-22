package com.sanyinggroup.corp.urocissa.server.test;

import org.junit.Test;

import com.sanyinggroup.corp.urocissa.server.api.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.server.api.service.MsgEvent;
import com.sanyinggroup.corp.urocissa.server.api.service.MsgServiceHandler;
import com.sanyinggroup.corp.urocissa.server.api.service.MsgServiceHandlerRegister;
import com.sanyinggroup.corp.urocissa.server.test.common.Test1;
import com.sanyinggroup.corp.urocissa.server.test.common.Test2;

public class MsgServiceHandlerRegisterTest {
	@Test
	public void test() {
		MsgServiceHandlerRegister register =MsgServiceHandlerRegister.getRegister();
		//注册消息处理器
		register.addMsgServiceHandler("test1", new MsgServiceHandler() {
			
			@Override
			public MiddleMsg handleMsgEvent(MsgEvent dm, MiddleMsg msg) {
				return msg;
			}
		});
		//注册消息处理器
		register.addMsgServiceHandler("test2", Test2.class);
		
		System.out.println("====================");
		MsgServiceHandler msgServiceHandler = register.getMsgServiceHandler("test1");
		System.out.println(msgServiceHandler);
	}
}
