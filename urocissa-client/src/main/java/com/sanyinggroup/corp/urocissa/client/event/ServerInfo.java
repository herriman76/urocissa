package com.sanyinggroup.corp.urocissa.client.event;
/**
 * 服务器信息
 * @author lixiao create at 2018年1月16日 下午4:41:38 
 * @since 2.0.0
 */
public class ServerInfo {
	private String ip; // 监听ip
	private int port ; // 监听端口
	private String appKey;
	
	public ServerInfo(String ip, int port, String appKey) {
		super();
		this.ip = ip;
		this.port = port;
		this.appKey = appKey;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	@Override
	public String toString() {
		return "appKey=" + appKey+" ，连接的服务端信息 [ip=" + ip + ", port=" + port + "]";
	}
}
