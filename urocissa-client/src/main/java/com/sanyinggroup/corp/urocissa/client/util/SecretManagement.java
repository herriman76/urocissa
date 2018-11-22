package com.sanyinggroup.corp.urocissa.client.util;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * <p>Package:com.sanyinggroup.communication.client.util</p> 
 * <p>Title:SecretManagement</p> 
 * <p>Description: 密码交换管理工具类</p> 
 * @author lixiao
 * @date 2017年8月29日 下午2:09:26
 * @version
 */
public class SecretManagement {
	private static final transient Logger logger = LoggerFactory.getLogger(SecretManagement.class);
	private String sessionId;
	private String appKey;
	private String appSecret; //
	private String prevSecret; // 前一个密码
	private String presentSecret; //当前密码
	private String nextSecret; //下一个密码
	private Date changingTime; // 密码更换时间
	private long expiresSecs = 600; // 过期时长
	
	public SecretManagement() {
		super();
	}
	
	public SecretManagement(String appKey, String appSecret) {
		super();
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.presentSecret = appSecret; //初始化设置当前密码为 appSecret
		this.changingTime = new Date();
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
		return presentSecret;
	}
	/**
	 * @param presentSecret the presentSecret to set
	 */
	public void setPresentSecret(String presentSecret) {
		setPresentSecret(presentSecret, expiresSecs); //默认十分钟还一次
	}
	public void setPresentSecret(String presentSecret,long expiresSecs) {
		if(expiresSecs<=10){
			expiresSecs = 10;
			logger.error("密码交换间隔最少为10S，置为最小值");
		}
		this.prevSecret = this.presentSecret;
		this.presentSecret = presentSecret;
		this.changingTime = new Date();
		this.expiresSecs= expiresSecs;
	}
	public void nextToPresent(){
		setPresentSecret(getNextSecret());
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
	public void setNextSecret(String nextSecret) {
		this.nextSecret = nextSecret;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SecretManagement [sessionId=" + sessionId + ", appKey=" + appKey + ", appSecret=" + appSecret
				+ ", prevSecret=" + prevSecret + ", presentSecret=" + presentSecret + ", nextSecret=" + nextSecret
				+ "]";
	}
	
	
	
}
