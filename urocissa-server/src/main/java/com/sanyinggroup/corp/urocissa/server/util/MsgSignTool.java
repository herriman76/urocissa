package com.sanyinggroup.corp.urocissa.server.util;

import java.util.HashMap;
import java.util.Map;

import com.sanyinggroup.corp.urocissa.core.util.SignTool;
import com.sanyinggroup.corp.urocissa.server.api.model.MiddleMsg;


/**
 * <p>Package:com.sanyinggroup.corp.urocissa.core.util</p> 
 * <p>Title:MsgSignTool</p> 
 * <p>Description: 中间件对消息消息签名</p> 
 * @author lixiao
 * @date 2017年8月29日 下午4:44:56
 * @version 1.0.0
 * @since 1.0.0
 */
public class MsgSignTool {
	/**
	 * <p>Title:verifySign</p> 
	 * <p>Description:验证消息签名是否正确</p> 
	 * @date 2017年8月29日 下午5:51:16
	 * @return boolean
	 * @param appSecret
	 * @param msg
	 * @return
	 * @since
	 */
	public static boolean verifySign(String appSecret, MiddleMsg msg){
		return SignTool.signVerify(appSecret, MsgToMap(msg));
	}
	/**
	 * <p>Title:sign</p> 
	 * <p>Description: 获取消息签名</p> 
	 * @date 2017年8月29日 下午5:51:50
	 * @return String
	 * @param appSecret
	 * @param msg
	 * @since
	 */
	public static String sign(String appSecret, MiddleMsg msg){
		Map<String ,String> params = MsgToMap(msg);
		params.put("appSecret", appSecret==null?"":appSecret);
		return SignTool.sign(params);
	}
	/**
	 * <p>Title:MsgToMap</p> 
	 * <p>Description:将 MiddleMsg转成sign所需要的参数</p> 
	 * @date 2017年8月29日 下午6:30:10
	 * @return Map<String,String>
	 * @param msg
	 * @return
	 * @since
	 */
	private static Map<String ,String> MsgToMap(MiddleMsg msg){
		Map<String ,String> params = new HashMap<String,String>();
		params.put("sessionId", msg.getHeader().getSessionID()==null?"":msg.getHeader().getSessionID());
		params.put("appKey", msg.getHeader().getAppKey()==null?"":msg.getHeader().getAppKey());
		params.put("timestamp", msg.getHeader().getTimestamp()+"");
		//params.put("length", msg.getHeader().getLength()+"");
		params.put("msgId", msg.getHeader().getMsgId());
		params.put("sign", msg.getHeader().getSign()==null?"":msg.getHeader().getSign());
		return params;
	}
	
	
}
