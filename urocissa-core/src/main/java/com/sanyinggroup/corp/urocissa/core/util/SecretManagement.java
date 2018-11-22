package com.sanyinggroup.corp.urocissa.core.util;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
/**
 * <p>Package:com.sanyinggroup.corp.urocissa.core.util</p> 
 * <p>Title:SecretManagement</p> 
 * <p>Description: 密码交换管理工具类</p> 
 * @author lixiao
 * @date 2017年8月29日 下午2:09:26
 * @version
 */
public class SecretManagement {
	private static final transient Logger logger = LoggerFactory.getLogger(SecretManagement.class);
	private String sessionId;
	private long timestamp;
	private String appKey;
	private String appSecret; //
	private String prevSecret; // 前一个密码
	private String presentSecret; //当前密码
	private String nextSecret; //下一个密码
	private Date changingTime; // 密码更换时间
	private long expiresSecs = 120; // 过期时长 （秒）
	private long nextGenTime; //下一个密码生成时间
	private transient Channel channel;
	/**
	 * @since 2.0.0
	 */
	private String ip;
	/**
	 * @since 2.0.0
	 */
	private int port;
	
	public SecretManagement() {
		super();
	}
	
	public SecretManagement(String appKey, String appSecret) {
		super();
		this.appKey = appKey;
		this.prevSecret = appSecret;
		this.appSecret = appSecret;
		this.presentSecret = appSecret; //初始化设置当前密码为 appSecret
		this.changingTime = new Date();
		// server端独有
		setNextSecret(); // 设置下一个密码
		
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
	 * @return the appKey
	 */
	public String getAppKey() {
		return appKey;
	}
	/**
	 * @param appKey the appKey to set
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
	 * @param appSecret the appSecret to set
	 */
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
		this.presentSecret = appSecret; //初始化设置当前密码为 appSecret
		this.changingTime = new Date();
	}
	/**
	 * @return the prevSecret
	 */
	public String getPrevSecret() {
		return prevSecret;
	}
	
	/**
	 * @return the presentSecret
	 */
	public String getPresentSecret() {
		//当前密码过期，重新设置
		if((new Date().getTime()-changingTime.getTime())/1000>expiresSecs){
			setNextToPresentSecret();
		}
		return presentSecret;
	}
	/**
	 * @param presentSecret the presentSecret to set
	 */
	private void setPresentSecret(String presentSecret) {
		setPresentSecret(presentSecret, expiresSecs); //默认十分钟还一次
	}
	private void setPresentSecret(String presentSecret,long expiresSecs) {
		if(expiresSecs<=10){
			expiresSecs = 10;
			logger.error("密码交换间隔最少为10S，置为最小值");
		}
		this.prevSecret = this.presentSecret;
		this.presentSecret = presentSecret;
		this.changingTime = new Date();
		this.expiresSecs= expiresSecs;
		setNextSecret();// 设置一下下一个密码
	}
	/**
	 * <p>Title:setNextToPresentSecret</p> 
	 * <p>Description: 将下一个密码转化为当前密码，再重新生成下一个密码  server端独有</p> 
	 * @date 2017年9月4日 下午2:03:02
	 * @return void
	 * @since
	 */
	private void setNextToPresentSecret(){
		if(null == nextSecret){
			setNextSecret();
		}
		setPresentSecret(getNextSecret()); //将下一个密码置为当前密码
		
	}
	/**
	 * @return the nextSecret
	 */
	public String getNextSecret() {
		return nextSecret;
	}
	/**
	 * @param nextSecret the nextSecret to set
	 */
	@SuppressWarnings("unused")
	private void setNextSecret(String nextSecret) {
		this.nextSecret = nextSecret;
	}
	/**
	 * <p>Title:setNextSecret</p> 
	 * <p>Description:server端独有 </p> 
	 * @date 2017年9月4日 下午2:06:38
	 * @return void
	 * @since
	 */
	private String setNextSecret() {
		if(this.nextSecret ==null || (new Date().getTime()-nextGenTime)/1000>expiresSecs/2){
			nextGenTime = new Date().getTime();
			this.nextSecret = MD5.toMD5(nextGenTime+"");
		}
		return this.nextSecret;
	}
	/**
	 * @return the changingTime
	 */
	public Date getChangingTime() {
		return changingTime;
	}
	/**
	 * @param changingTime the changingTime to set
	 */
	public void setChangingTime(Date changingTime) {
		this.changingTime = changingTime;
	}
	/**
	 * @return the expiresSecs
	 */
	public long getExpiresSecs() {
		return expiresSecs;
	}
	/**
	 * @param expiresSecs the expiresSecs to set
	 */
	public void setExpiresSecs(long expiresSecs) {
		this.expiresSecs = expiresSecs;
	}
	
	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
	/**
	 * @return the channel
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "SecretManagement [sessionId=" + sessionId + ", appKey=" + appKey + ", appSecret=" + appSecret
				+ ", prevSecret=" + prevSecret + ", presentSecret=" + presentSecret + ", nextSecret=" + nextSecret
				+ ", changingTime=" + changingTime + ", expiresSecs=" + expiresSecs + "]";
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
	
	
	
}
