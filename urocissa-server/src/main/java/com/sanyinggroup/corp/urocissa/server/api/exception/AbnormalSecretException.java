package com.sanyinggroup.corp.urocissa.server.api.exception;
/**
 * <p>Package:com.sanyinggroup.communication.server.api.exception</p> 
 * <p>Title:AbnormalSecretException</p> 
 * <p>Description: 密码异常</p> 
 * @author lixiao
 * @date 2017年8月17日 下午1:26:03
 * @version
 */
public class AbnormalSecretException extends RuntimeException {

	private static final long serialVersionUID = 8792964494100238941L;
	
	public AbnormalSecretException(){
		super();
	}
	public AbnormalSecretException(String s){
		super(s);
	}
}
