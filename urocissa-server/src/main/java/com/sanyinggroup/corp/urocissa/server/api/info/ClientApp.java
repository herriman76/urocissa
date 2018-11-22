package com.sanyinggroup.corp.urocissa.server.api.info;

import com.sanyinggroup.corp.urocissa.server.api.model.ClientDeviceInfo;

/**
 * 
 * <p>Package:com.sanyinggroup.communication.server.api.info</p> 
 * <p>Title:ClientApp</p> 
 * <p>Description: 客户端app信息</p> 
 * @author lixiao
 * @date 2017年8月2日 上午10:02:52
 * @version
 */
public class ClientApp {
	
	public static final int ENABLE=0; // 可用
	public static final int DISABLE=-1; //不可用
	private String appKey; //appkey
	
	private String appSecret; //appKey
	
	private int status; //状态值  0 可用 -1 不可用
	
	private String ip;
	
	private String sessionId;
	
	private String channelId;
	
	private ClientDeviceInfo deviceInfo; //设备id
	//private SecretManagement man;
	
	
	
	public ClientApp() {
		super();
		//man =  new SecretManagement();
	}
	/**
	 * @param appKey
	 * @param appSecret
	 * @param status :ClientApp.ENABLE or ClientApp.DISABLE
	 */
	public ClientApp(String appKey, String appSecret, int status) {
		super();
		this.appKey = appKey;
		this.appSecret = appSecret;
		if(status!=0 && status!=-1){
			new IllegalArgumentException("状态值不合法，仅 0 和-1，0  可用 -1 不可用");
		}else
			this.status = status;
		//this.man = new SecretManagement(appKey, appSecret);
	}
	public ClientApp(String appKey, String appSecret) {
		 this(appKey,appSecret,ENABLE);
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
		//this.man.setAppKey(appKey);
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
		//this.man.setAppSecret(appSecret);
	}

	public int getStatus() {
		return status;
	}
	/**
	 * 
	 * <p>Title:setStatus</p> 
	 * <p>Description: 设置当前clientApp状态   取值 @see ENABLE and  DISABLE</p> 
	 * @date 2017年8月2日 上午10:04:51
	 * @version 
	 * @return void
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the domain
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * @param domain the domain to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}
	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	/**
	 * @return the channelId
	 */
	public String getChannelId() {
		return channelId;
	}
	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	
	public ClientDeviceInfo getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(ClientDeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	
	

	
}
