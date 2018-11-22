package com.sanyinggroup.corp.urocissa.server.api.exception;

public class NoMsgServiceHandlerFound extends RuntimeException {

	private static final long serialVersionUID = -4072221740272848167L;
	
	public NoMsgServiceHandlerFound(){
		super();
	}
	public NoMsgServiceHandlerFound(String s){
		super(s);
	}
	
}
