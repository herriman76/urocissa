package com.sanyinggroup.corp.urocissa.client.api.handler;

import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;

public  class PushReceiveHandler implements PushReceiver{
	
	public MiddleMsg handleReceivedMsg(MiddleMsg msg){
		System.out.println(msg);
		return msg;
	}
}
