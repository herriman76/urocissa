package com.sanyinggroup.corp.urocissa.server.api.handler;

import com.sanyinggroup.corp.urocissa.server.api.model.MiddleMsg;

/**
 * <p>Package:com.sanyinggroup.communication.server.api.handler</p> 
 * <p>Title:ServerPushCallBack</p> 
 * <p>Description:服务端推送后的回调 </p> 
 * @author lixiao
 * @date 2017年10月18日 上午11:19:12
 * @version 
 * @since 1.0.3
 */
public interface ServerPushCallback {
	public void callback(MiddleMsg msg);
}
