package com.sanyinggroup.corp.urocissa.server.util;
/**
 * appkey  和  ip 组合 ，黑名单辅助类
 * @author lixiao create at 2018年1月18日 下午4:10:12 
 * @since 1.0.0
 */
public class AppKeyWithIp {
	private String appKey;
	private String ip;
	
	
	public AppKeyWithIp(String appKey, String ip) {
		super();
		this.appKey = appKey;
		this.ip = ip;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
}
