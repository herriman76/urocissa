package com.sanyinggroup.corp.urocissa.client.test;

import com.sanyinggroup.corp.urocissa.client.event.EventHanlerInterface;
import com.sanyinggroup.corp.urocissa.client.event.ServerInfo;
import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.client.model.ResultObject;

public abstract class EventHanlder implements EventHanlerInterface{

	@Override
	public void loginSuccess(ResultObject res) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loginError(ResultObject res) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnected(ServerInfo res) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgSendFail(MiddleMsg msg, ResultObject res) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectFail(ResultObject res) {
		// TODO Auto-generated method stub
		
	}

}
