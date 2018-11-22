package com.sanyinggroup.corp.urocissa.client.model;

import java.util.Map;
/**
 * <p>Package:com.sanyinggroup.communication.client.api.msg</p> 
 * <p>Title:ResultObject</p> 
 * <p>Description: 返回结果对象 </p> 
 * @author lixiao
 * @date 2017年8月24日 下午5:32:53
 * @version
 */
public class ResultObject {
	
	private int status; // 状态码
	private String statusMsg; //状态信息
	private Throwable cause; //异常信息
	private Map<String,Object> attachment; //附加值
	
	public static ResultObject getSuccessObject(){
		return new ResultObject(200, "success");
	}
	public static ResultObject getErrorObject(){
		return new ResultObject(-1, "error");
	}
	
	public ResultObject() {
		super();
	}
	/**
	 * @param status 状态码
	 * @param statusMsg 状态信息
	 */
	public ResultObject(int status, String statusMsg) {
		super();
		this.status = status;
		this.statusMsg = statusMsg;
	}
	/**
	 * @param status 状态码
	 * @param statusMsg 状态信息
	 * @param cause 异常信息
	 */
	public ResultObject(int status, String statusMsg, Throwable cause) {
		super();
		this.status = status;
		this.statusMsg = statusMsg;
		this.cause = cause;
	}
	/**
	 * @param status 状态码
	 * @param statusMsg 状态信息
	 * @param attachment 附加值
	 */
	public ResultObject(int status, String statusMsg, Map<String, Object> attachment) {
		super();
		this.status = status;
		this.statusMsg = statusMsg;
		this.attachment = attachment;
	}
	
	/**
	 * @param status 状态码
	 * @param statusMsg 状态信息
	 * @param cause 异常信息
	 * @param attachment 附加值
	 */
	public ResultObject(int status, String statusMsg, Throwable cause, Map<String, Object> attachment) {
		super();
		this.status = status;
		this.statusMsg = statusMsg;
		this.cause = cause;
		this.attachment = attachment;
	}

	/**
	 * @return the status 状态码
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 * 状态码
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the statusMsg
	 * 状态信息
	 */
	public String getStatusMsg() {
		return statusMsg;
	}
	/**
	 * @param statusMsg the statusMsg to set
	 * 状态信息
	 */
	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}
	/**
	 * @return the cause
	 * 异常信息
	 */
	public Throwable getCause() {
		return cause;
	}
	/**
	 * @param cause the cause to set
	 * 异常信息
	 */
	public void setCause(Throwable cause) {
		this.cause = cause;
	}
	/**
	 * @return the attachment
	 *  附加值
	 */
	public Map<String, Object> getAttachment() {
		return attachment;
	}
	/**
	 * @param attachment the attachment to set
	 * 附加值
	 */
	public void setAttachment(Map<String, Object> attachment) {
		this.attachment = attachment;
	}
	
	
}
