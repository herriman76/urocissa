package com.sanyinggroup.corp.urocissa.client.api.handler;

import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;

public interface PushReceiver {
	public MiddleMsg handleReceivedMsg(MiddleMsg msg);
}
