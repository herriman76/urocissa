package com.sanyinggroup.corp.urocissa.server.event;

import com.sanyinggroup.corp.urocissa.server.api.info.ClientApp;
/**
 * 事件通知信息
 * @author lixiao create at 2018年1月16日 下午4:59:58 
 * @since 1.0.0
 */
public class EventInfo {
	private int status; // 状态码
	private String statusMsg; //状态信息
	private Throwable cause; //异常信息
	private ClientApp appinfo; //客户端信息
	
	
	public EventInfo() {
		super();
	}
	public EventInfo(int status, String statusMsg, ClientApp appinfo) {
		super();
		this.status = status;
		this.statusMsg = statusMsg;
		this.appinfo = appinfo;
	}
	public EventInfo(int status, String statusMsg, Throwable cause, ClientApp appinfo) {
		super();
		this.status = status;
		this.statusMsg = statusMsg;
		this.cause = cause;
		this.appinfo = appinfo;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getStatusMsg() {
		return statusMsg;
	}
	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}
	public Throwable getCause() {
		return cause;
	}
	public void setCause(Throwable cause) {
		this.cause = cause;
	}
	public ClientApp getAppinfo() {
		return appinfo;
	}
	public void setAppinfo(ClientApp appinfo) {
		this.appinfo = appinfo;
	}
	
}
