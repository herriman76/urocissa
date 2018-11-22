package com.sanyinggroup.corp.urocissa.client;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.core.util.PropertyFileHandle;


/**
 * <p>Package:com.sanyinggroup.communication.client</p> 
 * <p>Title:ServerConfig</p> 
 * <p>Description: 客户端参数配置以及服务器端ip和端口配置配置</p> 
 * @author lixiao
 * @date 2017年7月19日 下午6:11:15
 * @version
 */
public class ClientConfig {
	private static final Logger logger = LoggerFactory.getLogger(ClientConfig.class);
	private static String ip ; //监听ip
	private static int port = 9166; // 监听端口
	private static String appKey; 
	private static String appSecret;
	public static int serverStatus =0; // 服务器状态
	public static String desKey="!QA@WS#ED"; //des 秘钥
	private static boolean  isInit= false; // 是否初始化
	
	
	
	
	
	/**
	 * @return the isInit
	 */
	public static boolean isInit() {
		return isInit;
	}
	
	/**
	 * @return the ip
	 */
	public static String getIp() {
		return ip;
	}
	/**
	 * @param ip the ip to set
	 */
	public static void setIp(String ip) {
		ClientConfig.ip = ip;
	}
	/**
	 * @return the port
	 */
	public static int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public static void setPort(int port) {
		ClientConfig.port = port;
	}
	/**
	 * @return the appKey
	 */
	public static String getAppKey() {
		return appKey;
	}
	/**
	 * @param appKey the appKey to set
	 */
	public static void setAppKey(String appKey) {
		ClientConfig.appKey = appKey;
	}
	/**
	 * @return the appSecret
	 */
	public static String getAppSecret() {
		return appSecret;
	}
	/**
	 * @param appSecret the appSecret to set
	 */
	public static void setAppSecret(String appSecret) {
		ClientConfig.appSecret = appSecret;
	}
	/**
	 * <p>Title:initWithConfig</p> 
	 * <p>Description: 根据配置文件初始化</p> 
	 * @date 2017年7月20日 下午3:02:25
	 * @version 
	 * @return void
	 * @param configPath
	 * @param appSecret
	 * @throws IOException
	 */
	public  static void  initWithConfig(String configPath,String appSecret) throws IOException{
		Map<String,String> map = PropertyFileHandle.read(configPath);
		logger.info("启动配置信息：\n"+map);
		//设置端口
		if(map.get("port")!=null && !("").equals(map.get("port"))){
			port = Integer.parseInt(map.get("port"));
		}
		if(map.get("ip")!=null && !("").equals(map.get("ip"))){
			ip = map.get("ip");
		}
		if(map.get("appKey")!=null && !("").equals(map.get("appKey"))){
			appKey = map.get("appKey");
		}
	    ClientConfig.appSecret = appSecret;
	    isInit = true;
	}
	/**
	 * 
	 * <p>Title:init</p> 
	 * <p>Description: 默认初始化，加载clientConfig.properties</p> 
	 * @date 2017年7月20日 下午3:02:04
	 * @version 
	 * @return void
	 * @param appSecret
	 * @throws IOException
	 */
	public static void  init(String appSecret) throws IOException{
		initWithConfig("clientConfig.properties", appSecret);
	}
	/**
	 * 
	 * <p>Title:init</p> 
	 * <p>Description: </p> 
	 * @date 2017年7月20日 下午3:17:12
	 * @version 
	 * @return void
	 * @param appKey
	 * @param appSecret
	 * @param remotePort 远程端口
	 * @param remoteIp  远程ip
	 * @throws IOException
	 */
	public static void  init(String appKey,String appSecret,int remotePort,String remoteIp) throws IOException{
		ClientConfig.appKey = appKey;
		ClientConfig.appSecret = appSecret;
		ClientConfig.port = remotePort;
		ClientConfig.ip = remoteIp;
		isInit = true;
	}
	
	
	
	/*public static void main(String[] args) throws IOException {
		//System.out.println(PropertyFileHandle.read("config.properties"));
		init();
	}*/
	
	
	
}
