package com.sanyinggroup.corp.urocissa.client.init;


/**
 * <p>Package:com.sanyinggroup.corp.urocissa.client.init</p> 
 * <p>Title:StartConfig</p> 
 * <p>Description: 客户端初始化参数</p> 
 * @author lixiao
 * @date 2017年8月17日 下午4:34:43
 * @version
 */
public class StartConfig {
	private String ip; // 监听ip
	private int port = 9166; // 监听端口
	private String appKey;
	private String appSecret;
	public int serverStatus = 0; // 服务器状态
	
	/**
	 * @param ip 远程server端ip
	 * @param port 远程server 端口
	 * @param appKey 服务端分配的appKey
	 * @param appSecret 服务端分配的appSecret
	 */
	public StartConfig(String ip, int port, String appKey, String appSecret) {
		super();
		//this.ip = ip;
		setIp(ip);
		//this.port = port;
		setPort(port);
		this.appKey = appKey;
		this.appSecret = appSecret;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip 远程server端ip
	 *            the ip to set
	 */
	public void setIp(String ip) {
		/*if(!IPUtil.isIPV4(ip)){
			throw new IllegalArgumentException("ipV4参数错误");
		}*/
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		if(port<=0 || port >65535){
			throw new IllegalArgumentException("端口参数错误,范围0 - 65535");
		}
		this.port = port;
	}

	/**
	 * @return the appKey
	 */
	public String getAppKey() {
		return appKey;
	}

	/**
	 * @param appKey
	 *            the appKey to set
	 */
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	/**
	 * @return the appSecret
	 */
	public String getAppSecret() {
		return appSecret;
	}

	/**
	 * @param appSecret
	 *            the appSecret to set
	 */
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	/**
	 * @return the serverStatus
	 */
	public int getServerStatus() {
		return serverStatus;
	}

	/**
	 * @param serverStatus
	 *            the serverStatus to set
	 */
	public void setServerStatus(int serverStatus) {
		this.serverStatus = serverStatus;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StartConfig [ip=" + ip + ", port=" + port + ", appKey="
				+ appKey + ", appSecret=" + appSecret + ", serverStatus="
				+ serverStatus + "]";
	}
	
	

}
