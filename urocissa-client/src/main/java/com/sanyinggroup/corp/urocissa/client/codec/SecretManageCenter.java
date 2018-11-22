package com.sanyinggroup.corp.urocissa.client.codec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sanyinggroup.corp.urocissa.client.util.SecretManagement;


/**
 * <p>Package:com.sanyinggroup.corp.urocissa.client.codec</p> 
 * <p>Title:SecretManageCenter</p> 
 * <p>Description: </p> 
 * @author lixiao
 * @date 2017年8月29日 下午3:56:49
 * @version 1.0.0
 */
public class SecretManageCenter {
	private static Map<String,SecretManagement> secretCenter = new ConcurrentHashMap<String, SecretManagement>(2);
	/**
	 * 
	 * <p>Title:getSecretMan</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月29日 下午4:09:30
	 * @return SecretManagement
	 * @param key
	 * @since
	 */
	protected  static SecretManagement getSecretMan(String key) {
		return secretCenter.get(key);
	}
	/**
	 * <p>Title:setSecretMan</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月29日 下午4:10:28
	 * @return SecretManagement
	 * @param key
	 * @param man
	 * @since
	 */
	protected static SecretManagement setSecretMan(String key,SecretManagement man) {
		return secretCenter.put(key, man);
	}
}
