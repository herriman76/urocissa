package com.sanyinggroup.corp.urocissa.server.api.service;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.server.event.AbstractEventHandler;

/**
 * 
 * <p>Package:com.sanyinggroup.communication.server.api.service</p> 
 * <p>Title:MsgServiceHandlerRegister</p> 
 * <p>Description: handler注册器</p> 
 * @author lixiao
 * @date 2017年8月3日 下午3:38:22
 * @version
 */
public class MsgServiceHandlerRegister {
	//private static volatile Vector<MsgListener> repository = new Vector<MsgListener>();//监听自己的监听器队列  
	private static final Logger logger = LoggerFactory.getLogger(MsgServiceHandlerRegister.class);
	private static volatile Map<String,MsgServiceHandler> serviceHandlers =new Hashtable<String,MsgServiceHandler>();
	/**
	 * @since 2.0.0
	 */
	private static volatile Map<String, Class<MsgServiceHandler>> serviceHandlersClass =new Hashtable<String,Class<MsgServiceHandler> >();
	/**
	 * 事件驱动类
	 * @since 2.0.0
	 */
	private static Class<? extends AbstractEventHandler> eventHandlerClass;
	private static MsgServiceHandlerRegister register;
	private static Boolean lock=true;
	//private static List<Integer> ports = new LinkedList<Integer>();
	
	
	private MsgServiceHandlerRegister(){
		super();
	}
	/**
	 * 获取handler注册器
	 * @author lixiao create at 2018年1月12日 下午5:41:26 
	 * @since 1.0.0
	 * @return
	 */
	public static MsgServiceHandlerRegister getRegister(){
		if(register==null){
			synchronized (lock) {
				if(register==null){
					register = new MsgServiceHandlerRegister();
				}
			}
		}
		return register;
	}
	/**
	 * <p>Title:addMsgServiceHandler</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月3日 下午3:38:51
	 * @version 
	 * @param actionName  
	 * @param serviceHandler
	 * @return MsgServiceHandlerRegister
	 * @see #addMsgServiceHandler(String, Class) 不会往class中加
	 */
	public MsgServiceHandlerRegister addMsgServiceHandler(String actionName,MsgServiceHandler serviceHandler) {  
		serviceHandlers.put(actionName,serviceHandler);
		//addMsgServiceHandler(actionName, (Class<?>) serviceHandler.getClass());
		//serviceHandlersClass.put(actionName, (Class<MsgServiceHandler>) serviceHandler.getClass());
        return this;
	} 
	/**
	 * 注册handler
	 * @author lixiao create at 2018年1月12日 下午4:58:12 
	 * @since 2.0.0
	 * @param actionName
	 * @param serviceHandler
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public MsgServiceHandlerRegister addMsgServiceHandler(String actionName,Class<?>  serviceHandler) {
		logger.debug("classname:"+ serviceHandler.getName());
		/*logger.debug("getCanonicalName:"+ serviceHandler.getCanonicalName());
		logger.debug("getSimpleName:"+ serviceHandler.getSimpleName());
		logger.debug("getTypeName:"+ serviceHandler.getTypeName());
		logger.debug("classname:"+ serviceHandler.getInterfaces());
		logger.debug("classname:");*/
		if(MsgServiceHandler.class.isAssignableFrom(serviceHandler)) {
			serviceHandlersClass.put(actionName, (Class<MsgServiceHandler>) serviceHandler);
		}else {
			logger.error("注册的类必须实现"+MsgServiceHandler.class.getName()+"接口");
		}
        return this;
	} 
	/**
	 * <p>Title:addMsgServiceHandler</p> 
	 * <p>Description:注册消息处理器 </p> 
	 * @date 2017年10月31日 上午11:37:26
	 * @return MsgServiceHandlerRegister
	 * @param handlers
	 * @see #addMsgServiceHandlerWithClass(Map)
	 * @since 1.0.0
	 */
	public MsgServiceHandlerRegister addMsgServiceHandler(Map<String,MsgServiceHandler> handlers) {
		if(handlers!=null) {
			serviceHandlers.putAll(handlers);
			Iterator<String> iterator = handlers.keySet().iterator();
			while(iterator.hasNext()) {
				String key = iterator.next();
				//serviceHandlersClass.put(key, (Class<MsgServiceHandler>) serviceHandlers.get(key).getClass());
				addMsgServiceHandler(key, (Class<?>) serviceHandlers.get(key).getClass());
			}
			
		}
        return this;
	}  
	/**
	 * 增加消息处理器
	 * @author lixiao create at 2018年1月12日 下午5:00:50 
	 * @since 2.0.0
	 * @param classname 
	 * @return
	 */
	public MsgServiceHandlerRegister addMsgServiceHandlerWithClass( Map<String, Class<MsgServiceHandler>> classnameMap) {
		if(classnameMap!=null)
			serviceHandlersClass.putAll(classnameMap);
		return this;
	}  
	/**
	 * <p>Title:getAllRegistedHandlers</p> 
	 * <p>Description:获取所有注册的Handler</p> 
	 * <p>不推荐使用了,里面少通过class注入进来的handler</p>
	 * @date 2017年7月18日 下午3:44:59
	 * @version 
	 * @return Map<String,MsgServiceHandler>
	 * @see #getAllRegistedHandlersWithClass()
	 */
	@Deprecated
	public Map<String,MsgServiceHandler> getAllRegistedHandlers(){
		return serviceHandlers;
	}
	/**
	 * <p>Description:获取所有注册的Handler</p> 
	 * @author lixiao create at 2018年1月12日 下午5:45:09 
	 * @since 2.0.0
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Class<MsgServiceHandler>> getAllRegistedHandlersWithClass(){
		Iterator<String> iterator = serviceHandlers.keySet().iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();
			serviceHandlersClass.put(key, (Class<MsgServiceHandler>) serviceHandlers.get(key).getClass());
		}
		return serviceHandlersClass;
	}
	
	
	/**
	 * <p>Title:getMsgServiceHandler</p> 
	 * <p>Description: 获取已通过名字注册的 消息处理类</p> 
	 * @date 2017年7月18日 下午3:59:21
	 * @version 
	 * @return MsgServiceHandler
	 * @param action
	 * @return
	 */
	public MsgServiceHandler getMsgServiceHandler(String action){
		if(action==null){
			throw  new NullPointerException("actionName is null");
		}
		if(serviceHandlers.get(action)!=null) {
			
			return serviceHandlers.get(action);
		}else if(serviceHandlersClass.get(action) != null) {
			try {
				logger.debug("action："+serviceHandlersClass.get(action));
				return serviceHandlersClass.get(action).newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				logger.error("消息处理器，实例创建失败",e);
			}
		}
		return null;
		
	}
	/**
	 * 移除action-name  class 和  对象都会被移除
	 * @author lixiao create at 2018年1月12日 下午5:35:11 
	 * @since 1.0.0
	 * @see #removeMsgServiceHandlerClass(String)
	 * @param action
	 * @return
	 */
	public Map<String,MsgServiceHandler> removeMsgServiceHandler(String action){
		if(action!=null){
			serviceHandlers.remove(action);
			serviceHandlersClass.remove(action);
		}
		return serviceHandlers;
	}
	/**
	 * 根据actionname  移除action
	 * @author lixiao create at 2018年1月12日 下午5:37:48 
	 * @since 2.0.0
	 * @param action
	 * @return
	 */
	public Map<String, Class<MsgServiceHandler>> removeMsgServiceHandlerClass(String action){
		if(action!=null){
			serviceHandlersClass.remove(action);
			serviceHandlers.remove(action);
		}
		return serviceHandlersClass;
	}
	/**
	 * 获取注册的事件驱动类
	 * @author lixiao create at 2018年1月16日 下午5:33:36 
	 * @since 2.0.0
	 * @return
	 */
	public static Class<? extends AbstractEventHandler> getEventHandlerClass() {
		return eventHandlerClass;
	}
	/**
	 * 注册事件驱动类
	 * @author lixiao create at 2018年1月16日 下午5:33:57 
	 * @since 1.0.0
	 * @param eventHandlerClass
	 */
	public static void setEventHandlerClass(Class<? extends AbstractEventHandler> eventHandlerClass) {
		MsgServiceHandlerRegister.eventHandlerClass = eventHandlerClass;
	}
	
}
