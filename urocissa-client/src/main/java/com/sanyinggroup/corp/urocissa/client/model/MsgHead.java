package com.sanyinggroup.corp.urocissa.client.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sanyinggroup.corp.urocissa.client.ClientConfig;
import com.sanyinggroup.corp.urocissa.client.util.DeviceUtil;

/**
 * @author lixiao
 */
public class MsgHead {
	public static final int VCODE= 0x202;
	private int versionCode = VCODE; //版本号，如1.0.3

    private int length;// 消息长度

    private String sessionID;// 会话ID

    private byte type;// 消息类型

    private String appKey;// 客户端key
    
    private String sign; //签名
    
    private long timestamp;// 时间搓
    
    private String action; //请求操作
    
    private String msgId ; //消息id
    
    private int status; //消息状态，留给调用者 
    
    
    private Map<String, Object> attachment = new HashMap<String, Object>(); // 附件
    
    public MsgHead(){
    	super();
    	this.appKey = ClientConfig.getAppKey();
    	this.msgId = UUID.randomUUID().toString().replaceAll("-", "");
    	attachment.put("deviceInfo", DeviceUtil.getDeviceInfoWithId());
    }
    /**
     * 设置消息id
     * @param msgId
     */
    public MsgHead(String msgId){
    	super();
    	this.appKey = ClientConfig.getAppKey();
    	this.msgId = msgId;
    }
	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getSessionID() {
		return sessionID ==null?"":sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID ==null?"":sessionID;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Map<String, Object> getAttachment() {
		return attachment;
	}

	public void setAttachment(Map<String, Object> attachment) {
		this.attachment = attachment;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	
	/**
	 * @return the msgId
	 */
	public String getMsgId() {
		return msgId;
	}
	/**
	 * 设置消息id，服务端会原样返回，留作消息验证和处理
	 * @param msgId the msgId to set
	 */
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	
	@Override
	public String toString() {
		return "MsgHead [versionCode=" + versionCode + ", length=" + length
				+ ", sessionID=" + sessionID + ", type=" + type + ", appKey="
				+ appKey + ", sign=" + sign + ", timestamp=" + timestamp
				+ ", action=" + action + ", msgId=" + msgId + ", status="
				+ status + ", attachment=" + attachment + "]"+ "@" + Integer.toHexString(hashCode());
	}
	
    
}
