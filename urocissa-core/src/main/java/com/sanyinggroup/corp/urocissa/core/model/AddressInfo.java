package com.sanyinggroup.corp.urocissa.core.model;

import java.net.InetSocketAddress;
/**
 * <p>Package:com.sanyinggroup.communication.server.api.msg</p> 
 * <p>Title:AddressInfo</p> 
 * <p>Description:远程地址信息 </p> 
 * @author lixiao
 * @date 2017年9月20日 下午2:55:07
 * @version 
 * @since 1.0.1
 */
public class AddressInfo {
	private InetSocketAddress remoteAddress;
	private InetSocketAddress LocalAddress;
	
	
	public AddressInfo() {
		super();
	}
	/**
	 * @param remoteAddress 远程地址
	 * @param localAddress 本地地址
	 */
	public AddressInfo(InetSocketAddress remoteAddress, InetSocketAddress localAddress) {
		super();
		this.remoteAddress = remoteAddress;
		LocalAddress = localAddress;
	}
	/**
	 * @return the remoteAddress
	 */
	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}
	/**
	 * @param remoteAddress the remoteAddress to set
	 */
	public void setRemoteAddress(InetSocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	/**
	 * @return the localAddress
	 */
	public InetSocketAddress getLocalAddress() {
		return LocalAddress;
	}
	/**
	 * @param localAddress the localAddress to set
	 */
	public void setLocalAddress(InetSocketAddress localAddress) {
		LocalAddress = localAddress;
	}
	
	
}
