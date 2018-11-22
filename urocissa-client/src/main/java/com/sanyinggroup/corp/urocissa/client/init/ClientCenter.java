package com.sanyinggroup.corp.urocissa.client.init;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sanyinggroup.corp.urocissa.client.codec.SecretManageCenter;
import com.sanyinggroup.corp.urocissa.client.event.AbstractEventHandler;
import com.sanyinggroup.corp.urocissa.client.util.DeviceUtil;
/**
 * <p>Package:com.sanyinggroup.corp.urocissa.client.init</p> 
 * <p>Title:ClientCenter</p> 
 * <p>Description: 客户端管理中心</p> 
 * @author lixiao
 * @date 2017年8月21日 下午5:58:39
 * @version 0.2.0
 */
public class ClientCenter extends SecretManageCenter{
	//客户端信息key：（ip+port+appKey）
	private static Map<String,Client> clientCenter = new ConcurrentHashMap<String,Client>(2);
	// key:channelId ,value 客户端信息key
	public static Map<String,String> channelWithKey = new ConcurrentHashMap<String,String>(2);
	
	
	/**
	 * 获取一个客户端，如果没有注册，会自动注册
	 * @author lixiao create at 2018年1月16日 下午3:55:59 
	 * @since 1.0.0
	 * @param ip
	 * @param port
	 * @param appKey
	 * @param appSecret
	 * @param eventHandlerClass 事件处理类
	 * @return
	 */
	public static synchronized Client getAClient(String ip, int port, String appKey, String appSecret,Class<? extends AbstractEventHandler>  eventHandlerClass)  {
		Client client = clientCenter.get(ip+port+appKey);
		if(client==null){
			/*SecretManagement man  = new SecretManagement(appKey,appSecret);
			SecretManageCenter.setSecretMan(ip+port+appKey, man); //设置初始秘钥
			*/
			client = new Client(ip, port, appKey, appSecret,eventHandlerClass);
			clientCenter.put(ip+port+appKey, client);
		}
		return client;
	}
	/**
	 * 
	 * @author lixiao create at 2018年5月8日 下午2:36:59 
	 * @since 2.0.2
	 * @param ip
	 * @param port
	 * @param appKey
	 * @param appSecret
	 * @param eventHandlerClass
	 * @param deviceno  设备号
	 * @return
	 */
	public static synchronized Client getAClient(String ip, int port, String appKey, String appSecret,Class<? extends AbstractEventHandler>  eventHandlerClass,String deviceno)  {
		if(deviceno!=null && !("").endsWith(deviceno.trim())) {
			DeviceUtil.setPrefix(deviceno);
		}
		return getAClient(ip, port, appKey, appSecret, eventHandlerClass);
	}
	/**
	 * 获取一个客户端，如果没有注册，会自动注册
	 * @author lixiao create at 2018年1月16日 下午3:53:55 
	 * @since 1.0.0
	 * @param ip
	 * @param port
	 * @param appKey
	 * @param appSecret
	 * @return
	 */
	public static synchronized Client getAClient(String ip, int port, String appKey, String appSecret){
		return getAClient(ip, port, appKey, appSecret,null);
	}
	/**
	 * 根据key获取client
	 * @author lixiao create at 2018年1月16日 下午4:37:19 
	 * @since 2.0.0
	 * @param key
	 * @return
	 */
	public static synchronized Client getAClient(String key){
		return clientCenter.get(key);
	}
	
	/**
	 * <p>Title:registerClient</p> 
	 * <p>Description: 注册客户端, 若在注册中心，已经注册，会终止之前注册的一系列活动，重新注册</p> 
	 * @date 2017年8月21日 下午6:00:42
	 * @since 2.0.0 
	 * @return Client
	 * @param ip
	 * @param port
	 * @param appKey
	 * @param appSecret
	 * @param eventHandlerClass 事件处理类
	 * @throws Exception
	 */
	public static  synchronized Client registerClient(String ip, int port, String appKey, String appSecret,Class<? extends AbstractEventHandler>  eventHandlerClass) throws Exception{
		removeAClient(ip, port, appKey, appSecret);
		return getAClient(ip, port, appKey, appSecret,eventHandlerClass);
	}
	/**
	 * 注册客户端, 若在注册中心，已经注册，会终止之前注册的一系列活动，重新注册
	 * @author lixiao create at 2018年1月16日 下午3:55:44 
	 * @since 1.0.0
	 * @param ip
	 * @param port
	 * @param appKey
	 * @param appSecret
	 * @return
	 * @throws Exception
	 */
	public static  synchronized Client registerClient(String ip, int port, String appKey, String appSecret) throws Exception{
		return registerClient(ip, port, appKey, appSecret, null);
	}
	/**
	 * <p>Title:removeAClient</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月21日 下午5:48:05
	 * @version 0.2
	 * @return void
	 * @param ip
	 * @param port
	 * @param appKey
	 * @param appSecret
	 * @throws Exception
	 */
	public static  void removeAClient(String ip, int port, String appKey, String appSecret) throws Exception {
		shutdown(ip+port+appKey);
	}
	/**
	 * <p>Title:shutdown</p> 
	 * <p>Description: 关闭某个客户端</p> 
	 * @date 2017年8月21日 下午5:56:56
	 * @version 
	 * @return void
	 * @param ip
	 * @param port
	 * @param appKey
	 * @param appSecret
	 * @throws Exception
	 */
	public static  void shutdown(String ip, int port, String appKey, String appSecret) throws Exception {
		shutdown(ip+port+appKey);
	}
	/**
	 * 
	 * <p>Title:shutdown</p> 
	 * <p>Description: 关闭</p> 
	 * @date 2017年8月21日 下午5:57:14
	 * @version 
	 * @return void
	 * @param key ： ip+port+appKey
	 */
	public static void shutdown(String key){
		Client client = clientCenter.get(key);
		if(client!=null){
			clientCenter.remove(key);
			client.shutdown();
		}
	}
	/**
	 * 停止一个客户端连接
	 * @author lixiao create at 2018年5月3日 下午9:14:17 
	 * @since 1.0.0
	 * @param ip
	 * @param port
	 * @param appKey
	 */
	public static void shutdown(String ip,int port,String appKey){
		 shutdown(ip+port+appKey);
	}
	/**
	 * <p>Title:shutdownAll</p> 
	 * <p>Description: 关闭所有客户端</p> 
	 * @date 2017年8月21日 下午5:55:46
	 * @version 
	 * @return void
	 */
	public static void shutdownAll(){
		Set<String> keys = clientCenter.keySet();
		for(String key:keys){
			shutdown(key);
		}
		
	}
	/**
	 * <p>Title:shutdown</p> 
	 * <p>Description: 关闭客户端</p> 
	 * @date 2017年9月28日 上午10:14:00
	 * @return void
	 * @param client
	 * @since
	 */
	public static void shutdown(Client client){
		if(client!=null){
			StartConfig config = client.getConfig();
			client.shutdown();
			clientCenter.remove(config.getIp()+config.getPort()+config.getAppKey());
		}
	}
	
	
}
