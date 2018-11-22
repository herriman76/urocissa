package com.sanyinggroup.corp.urocissa.client.model;

import java.util.Date;

import com.sanyinggroup.corp.urocissa.core.model.MessageType;
import com.sun.org.apache.bcel.internal.generic.FLOAD;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultValueProcessor;
/**
 * 
 * <p>Package:com.sanyinggroup.communication.server.api.msg</p> 
 * <p>Title:Msg</p> 
 * <p>Description: 中间件传输过程中的消息对象</p> 
 * @author lixiao
 * @date 2017年7月13日 下午6:41:01
 * @version
 */
@SuppressWarnings("restriction")
public class MiddleMsg {
	
	private MsgHead msgHead;
	private Object body;
	private static JsonConfig jsonConfig= new JsonConfig(); 
	
	static{
		 
	    //  设置字符串格式  
	    jsonConfig.registerDefaultValueProcessor(Integer.class, new DefaultValueProcessor() {  
	        @Override  
	        public Object getDefaultValue(@SuppressWarnings("rawtypes")Class type) {  
	            return 0;  
	        }  
	    });  
	    
	    jsonConfig.registerDefaultValueProcessor(Double.class, new DefaultValueProcessor() {  
	    	@Override  
	    	public Object getDefaultValue(@SuppressWarnings("rawtypes") Class type) {  
	    		return 0.0D;  
	    	}  
	    });  
	    jsonConfig.registerDefaultValueProcessor(Short.class, new DefaultValueProcessor() {  
	    	@Override  
	    	public Object getDefaultValue(@SuppressWarnings("rawtypes")Class type) {  
	    		return 0;  
	    	}  
	    });  
	    jsonConfig.registerDefaultValueProcessor(FLOAD.class, new DefaultValueProcessor() {  
	    	
			@Override  
	    	public Object getDefaultValue(@SuppressWarnings("rawtypes")Class type) {  
	    		return 0.0f;  
	    	}  
	    });  
	}
	public MiddleMsg(){
		super();
	}
	/**
	 * @param action 操作action 
	 * @param type  body体种的数据类型
	 * @param body  body
	 */
	public MiddleMsg(String action,Byte type,Object body){
		MsgHead msghead = new MsgHead();
		msghead.setAction(action);
		msghead.setType(type);
		msghead.setTimestamp(new Date().getTime());
		setBody(body);
		this.msgHead = msghead;
	}
	/**
	 * @param action :@see MsgHead.action
	 * @param body
	 */
	public MiddleMsg(String action,Object body){
		MsgHead msghead = new MsgHead();
		msghead.setAction(action);
		msghead.setType(MessageType.CLIENT_REQ.value());
		msghead.setTimestamp(new Date().getTime());
		setBody(body);
		this.msgHead = msghead;
	}
	/**
	 * 
	 * @param msgId    @see MsgHead.msgId
	 * @param action   @see MsgHead.action
	 * @param body
	 */
	public MiddleMsg(String msgId, String action,Object body){
		if(msgId==null){
			throw new NullPointerException("msgId");
		}
		MsgHead msghead = new MsgHead(msgId);
		msghead.setAction(action);
		msghead.setType(MessageType.CLIENT_REQ.value());
		msghead.setTimestamp(new Date().getTime());
		setBody(body);
		this.msgHead = msghead;
	}
	
	public MiddleMsg(MsgHead msgHead,Object body){
		super();
		this.msgHead = msgHead;
		setBody(body);
	}
	
	public MsgHead getMsgHead() {
		return msgHead;
	}
	public void setMsgHead(MsgHead msgHead) {
		this.msgHead = msgHead;
	}
	public MsgHead getHeader() {
		return msgHead;
	}
	public void setHeader(MsgHead msgHead) {
		this.msgHead = msgHead;
	}
	public Object getBody() {
		/*if(body==null) return body;
		try {
			return DesUtil.decrypt(body.toString(), ClientConfig.desKey);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;*/
		return body;
	}
	public void setBody(Object body) {
		if(body!=null){
			if(body instanceof java.lang.String  ||
					body instanceof java.lang.Integer ||
					body instanceof java.lang.Long ||
					body instanceof java.lang.Short ||
					body instanceof java.lang.Boolean ||
					body instanceof java.lang.Byte ||
					body instanceof java.lang.Double ||
					body instanceof java.lang.Float){
				this.body = body;
			}else{
				//JSONObject.fromObject(body,jsonConfig).toString();
				this.body = JSONObject.fromObject(body,jsonConfig).toString();
			}
		}else{
			this.body = body;
		}
		/*if(body!=null){
			if(body instanceof java.lang.String  ||
					body instanceof java.lang.Integer ||
					body instanceof java.lang.Long ||
					body instanceof java.lang.Short ||
					body instanceof java.lang.Boolean ||
					body instanceof java.lang.Byte ||
					body instanceof java.lang.Double ||
					body instanceof java.lang.Float){
				try {
					this.body = DesUtil.encrypt(body.toString(), ClientConfig.desKey);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				try {
					this.body = DesUtil.encrypt(JSONObject.fromObject(body,jsonConfig).toString(), ClientConfig.desKey);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}*/
		
		
	}
	@Override
	public String toString() {
		return "MiddleMsg [msgHead=" + msgHead + ", body=" + getBody() + "] "+ "@" + Integer.toHexString(hashCode()) ;
	}
	
	
	
}	
