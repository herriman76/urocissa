package com.sanyinggroup.corp.urocissa.server.event;

public class EventHandlerTest extends AbstractEventHandler{
	@Override
	public void loginSuccess(EventInfo res) {
		super.loginSuccess(res);
	}
	@Override
	public void disconnected(EventInfo res){
		super.disconnected(res);
	}
	@Override
	public void serverClose() {
		
		
	}
	
}
