package com.sanyinggroup.corp.urocissa.server.api.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.core.model.MessageType;
import com.sanyinggroup.corp.urocissa.server.api.exception.BudMsgHeadException;
import com.sanyinggroup.corp.urocissa.server.api.exception.NoMsgServiceHandlerFound;
import com.sanyinggroup.corp.urocissa.server.api.model.MiddleMsg;


/**
 * <p>Package:com.sanyinggroup.communication.server.util</p> 
 * <p>Title:MsgSource</p> 
 * <p>Description: 处理消息源 ,用于netty的消息handler的channelRead处理</p> 
 * <p>eg:<code> MsgServerSource source =new MsgServerSource();
 * 				Msg result  = source.notifyMsgServiceHandler(msg);
 * 	     </code>
 * </p>
 * @author lixiao
 * @date 2017年7月13日 下午1:50:03
 * @version 0.1
 */
public class MsgServerSource{
	private static Logger logger = LoggerFactory.getLogger(MsgServerSource.class);
	private MiddleMsg msg;
	
	public MsgServerSource(){
		super();
	}
	public MsgServerSource(MiddleMsg msg ){
		super();
		this.msg = msg;
	}
	
	@Deprecated
	public Map<String ,Object> notifyMsgServiceHandler() {//通知所有的消息处理器，都处理一遍 
		Map<String ,Object> res = new HashMap<String,Object>();
		//Set<String> set = getAllRegistedHandlers().keySet();
		Set<String> set = MsgServiceHandlerRegister.getRegister().getAllRegistedHandlers().keySet();
		Iterator<String> iterator = set.iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			MsgServiceHandler msgListener = MsgServiceHandlerRegister.getRegister().getMsgServiceHandler(key);
			res.put(key, msgListener.handleMsgEvent(new MsgEvent(this), msg)) ;
		}
		return res;
        
	}
	/**
	 * <p>Title:notifyMsgServiceHandler</p> 
	 * <p>Description: 通知业务消息处理器处理消息</p> 
	 * @date 2017年7月13日 下午6:24:31
	 * @version 1.0
	 * @return Msg
	 * @param msg
	 */
	public MiddleMsg notifyMsgServiceHandler( final MiddleMsg msg) {//通知相对于的处理器
		return notifyMsgServiceHandler(this, msg);
		
	}
	/**
	 * 
	 * <p>Title:notifyMsgServiceHandler</p> 
	 * <p>Description:通知业务消息处理器处理消息 </p> 
	 * @date 2017年9月20日 下午2:37:28
	 * @return MiddleMsg
	 * @param obj
	 * @param msg
	 * @since 1.0.1
	 */
	public MiddleMsg notifyMsgServiceHandler(Object obj, final MiddleMsg msg) {//通知相对于的处理器
		if(msg==null){
			throw new NullPointerException();
		}else if(msg.getMsgHead()==null || msg.getMsgHead().getAction()==null){
			throw new BudMsgHeadException("请求头为空");
		//since 1.0.3 处理推送客户端回调	
		}else if(msg.getHeader().getType() == MessageType.CLIENT_RESP_FOR_SERVICE_PUSH.value()){
			return msg;
		}else{
			//logger.debug("寻找请求action："+msg.getMsgHead().getAction());
			MsgServiceHandler handler =  MsgServiceHandlerRegister.getRegister().
					getMsgServiceHandler(msg.getMsgHead().getAction());
			if(handler !=null){
				logger.debug("\n "+msg.getMsgHead().getAction()+"消息处理中...");
				return handler.handleMsgEvent(new MsgEvent(obj), msg);
			}else{
				throw new NoMsgServiceHandlerFound("404");
			}
		}
		
	}
	
	
	
}
