package com.sanyinggroup.corp.urocissa.server.api.model;
/**
 * 客户端设备信息
 * @author lixiao create at 2018年5月9日 上午10:12:50 
 * @since 2.0.3
 */
public class ClientDeviceInfo {
	private   String osName ; // 系统名称
	private   String hostName ; //主机名
	private  String localIp; // ip
	private   String macAddress; //mac 地址
	private  String deviceId; //设备id
	
	
	public ClientDeviceInfo() {
		super();
	}
	/**
	 * @param osName 系统名称
	 * @param hostName 主机名
	 * @param localIp ip
	 * @param macAddress 地址
	 * @param deviceId 设备id
	 */
	public ClientDeviceInfo(String osName, String hostName, String localIp, String macAddress, String deviceId) {
		super();
		this.osName = osName;
		this.hostName = hostName;
		this.localIp = localIp;
		this.macAddress = macAddress;
		this.deviceId = deviceId;
	}
	public String getOsName() {
		return osName;
	}
	public void setOsName(String osName) {
		this.osName = osName;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getLocalIp() {
		return localIp;
	}
	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	@Override
	public String toString() {
		return "ClientDeviceInfo [osName=" + osName + ", hostName=" + hostName + ", localIp=" + localIp
				+ ", macAddress=" + macAddress + ", deviceId=" + deviceId + "]";
	}
	
	
}
