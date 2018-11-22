package com.sanyinggroup.corp.urocissa.client.api.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.client.api.future.SendFuture;
import com.sanyinggroup.corp.urocissa.client.api.future.SyncResponseMap;
import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
/**
 * <p>Package:com.sanyinggroup.corp.urocissa.client.api.handler</p> 
 * <p>Title:SyncMsgHandler</p> 
 * <p>Description: 同步消息处理器</p> 
 * @author lixiao
 * @date 2017年8月18日 下午5:32:55
 * @version
 */
public class SyncMsgHandler implements MsgHandler{
	private static final Logger logger = LoggerFactory.getLogger(SyncMsgHandler.class);
	@Override
	public void callback(MiddleMsg msg) {
		if(msg!=null && msg.getHeader()!=null && msg.getHeader().getMsgId()!=null){
			//logger.debug(SyncResponseMap.syncKey.get(msg.getHeader().getMsgId()));
			SendFuture<MiddleMsg> sendFuture = SyncResponseMap.syncKey.get(msg.getHeader().getMsgId());
			if(sendFuture!=null){
				sendFuture.setResponse(msg);
			}
			logger.debug("同步处理一条消息");
		}else{
			logger.debug("同步处理消息为空，未处理："+msg);
		}
	}

}
