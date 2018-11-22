package com.sanyinggroup.corp.urocissa.server.test.common;

import com.sanyinggroup.corp.urocissa.server.api.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.server.api.service.MsgEvent;
import com.sanyinggroup.corp.urocissa.server.api.service.MsgServiceHandler;

public class Test2  implements MsgServiceHandler {
	@Override
	public MiddleMsg handleMsgEvent(MsgEvent dm, MiddleMsg msg) {
		msg.setBody("服务器处理成功:"+msg.getBody());
		System.out.println("---"+msg);
		return msg;
	}
}