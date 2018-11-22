package com.sanyinggroup.corp.urocissa.server.api.service;
/**
 * 
 * <p>Package:com.sanyinggroup.communication.server.api.service</p> 
 * <p>Title:MsgEvent</p> 
 * <p>Description: 消息事件处理 </p> 
 * @author lixiao
 * @date 2017年7月14日 下午2:19:03
 * @version
 */
public class MsgEvent extends java.util.EventObject{

	
	private static final long serialVersionUID = 1L;

	public MsgEvent(Object source) {
		super(source);
	}
	/**
	 * 
	 * <p>Title:callback</p> 
	 * <p>Description: 可以用作消息回调的日志处理 ，后期留作备用</p> 
	 * @date 2017年7月18日 下午3:19:29
	 * @version 
	 * @return Object
	 * @param msg
	 * @return
	 */
	public Object callback(Object msg) {     
        System.out.println("This is callback method...");  
        return msg;
   }   

}
