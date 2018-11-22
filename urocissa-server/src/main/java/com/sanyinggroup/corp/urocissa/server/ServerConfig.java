package com.sanyinggroup.corp.urocissa.server;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.core.util.PropertyFileHandle;
import com.sanyinggroup.corp.urocissa.core.util.SecretManagement;
import com.sanyinggroup.corp.urocissa.server.api.info.ClientApp;
import com.sanyinggroup.corp.urocissa.server.api.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.server.util.MsgSignTool;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * 
 * <p>Package:com.sanyinggroup.communication.server</p> 
 * <p>Title:ServerConfig</p> 
 * <p>Description: 服务器端配置</p> 
 * @author lixiao
 * @date 2017年7月19日 下午6:11:15
 * @version 0.1
 */
public class ServerConfig {
	private static final Logger logger =LoggerFactory.getLogger(ServerConfig.class);
	private static String IP = "127.0.0.1"; //监听ip 默认本机 127.0.0.1
	private static int PORT = 9166; // 监听端口 默认 9166
	//白名单
	private static List<String> whiteList = new ArrayList<String>(); 
	//黑名单
	private static List<String> blackList = new ArrayList<String>();
	//入站规则  0 不做检查 1：白名单  2 ：黑名单
	private static int inboundRule = 0;
	private  static boolean isInit = false; //是否已经初始化    如果手动调用每个set方法设置参数，请把这个值设为true
	/**
	 * Map<appkey,Map<appKey,ClientApp>>
	 */
	private volatile static Map<String,ClientApp> appKeys =  new ConcurrentHashMap<String,ClientApp>();
	
	public static String getIP() {
		return IP;
	}
	/**
	 * <p>Title:setIP</p> 
	 * <p>Description: 设置服务器ip</p> 
	 * @date 2017年7月19日 下午6:16:48
	 * @version 
	 * @return void
	 * @param ip
	 */
	public static void setIP(String ip) {
		IP = ip==null?"127.0.0.1":ip;
	}
	public static int getPORT() {
		return PORT;
	}
	/**
	 * <p>Title:setPORT</p> 
	 * <p>Description: 设置启动端口</p> 
	 * @date 2017年7月19日 下午6:20:29
	 * @version 
	 * @return void
	 * @param port
	 */
	public static void setPORT(int port) {
		PORT =  port<=0?9166:port;
	}
	/**
	 * <p>Title:getWhiteList</p> 
	 * <p>Description: 获取当前白名单列表</p> 
	 * @date 2017年8月2日 上午9:52:50
	 * @version 
	 * @return List<String>
	 * @return
	 */
	public static List<String> getWhiteList() {
		return whiteList ==null?new ArrayList<String>() : whiteList;
	}
	/**
	 * <p>Title:setWhiteList</p> 
	 * <p>Description: 设置白名单 </p> 
	 * @date 2017年7月19日 下午6:20:53
	 * @version 
	 * @return void
	 * @param whiteList
	 */
	public static void setWhiteList(List<String> whiteList) {
		ServerConfig.whiteList = whiteList;
	}
	/**
	 * <p>Title:addWhiteList</p> 
	 * <p>Description: 增加白名列表</p> 
	 * @date 2017年8月2日 上午9:54:57
	 * @version 
	 * @return List<String>
	 * @param whiteList
	 * @return
	 */
	public static List<String> addWhiteList(List<String> whiteList) {
		ServerConfig.whiteList.addAll(whiteList);
		return ServerConfig.whiteList;
	}
	/**
	 * <p>Title:addWhite</p> 
	 * <p>Description: 增加白名单 </p> 
	 * @date 2017年8月2日 上午9:55:56
	 * @version 
	 * @return List<String>
	 * @param white
	 * @return
	 */
	public static List<String> addWhite(String white) {
		ServerConfig.whiteList.add(white);
		return ServerConfig.whiteList;
	}
	
	/**
	 * <p>Title:getBlackList</p> 
	 * <p>Description: 获取黑名单列表</p> 
	 * @date 2017年8月2日 上午9:59:00
	 * @version 
	 * @return List<String>
	 * @return
	 */
	public static List<String> getBlackList() {
		return blackList==null?new ArrayList<String>() : blackList;
	}
	/**
	 * <p>Title:setBlackList</p> 
	 * <p>Description: 设置黑名单</p> 
	 * @date 2017年8月2日 上午9:56:18
	 * @version 
	 * @return void
	 * @param blackList
	 */
	public static void setBlackList(List<String> blackList) {
		ServerConfig.blackList = blackList;
	}
	/**
	 * 
	 * <p>Title:addBlackList</p> 
	 * <p>Description: 增加黑名单列表</p> 
	 * @date 2017年8月2日 上午10:01:52
	 * @version 
	 * @return List<String>
	 * @param blackList
	 * @return
	 */
	public static List<String> addBlackList(List<String> blackList) {
		ServerConfig.blackList.addAll(blackList);
		return blackList;
	}
	/**
	 * <p>Title:addBlack</p> 
	 * <p>Description: 增加黑名单</p> 
	 * @date 2017年8月2日 上午10:02:11
	 * @version 
	 * @return void
	 * @param black
	 */
	public static void addBlack(String black) {
		ServerConfig.blackList.add(black);
	}
	/**
	 * <p>Title:getInboundRule</p> 
	 * <p>Description: 入站规则  0 不做检查 1：白名单  2 ：黑名单</p> 
	 * @date 2017年8月2日 上午9:48:19
	 * @version 
	 * @return int
	 */
	public static int getInboundRule() {
		return inboundRule;
	}
	/**
	 * 
	 * <p>Title:setInboundRule</p> 
	 * <p>Description: 入站规则  0  不做检查 1：白名单  2 ：黑名单</p> 
	 * @date 2017年8月2日 上午9:48:03
	 * @version 
	 * @return void
	 * @param inboundRule
	 */
	public static void setInboundRule(int inboundRule) {
		ServerConfig.inboundRule = inboundRule;
	}
	
	public static Map<String, ClientApp> getAppKeys() {
		return appKeys;
	}
	public static void setAppKeys(Map<String, ClientApp> appKeys) {
		ServerConfig.appKeys.clear();
		ServerConfig.appKeys.putAll(appKeys);
	}
	/**
	 * 设置允许连接的客户端，会替换掉之前所有的设置
	 * @author lixiao create at 2018年5月9日 下午5:29:39 
	 * @since 2.0.3
	 * @param apps
	 */
	public static void setAppKeys(List<ClientApp> apps) {
		Map<String, ClientApp> tem = new HashMap<String, ClientApp>();
		if(apps !=null ) {
			for(ClientApp app:apps) {
				tem.put(app.getAppKey(), app);
			}
		}
		ServerConfig.appKeys.clear();
		ServerConfig.appKeys.putAll(tem);
	}
	/**
	 * 添加允许连接的客户端
	 * @author lixiao create at 2018年5月9日 下午5:29:39 
	 * @since 2.0.3
	 * @param apps
	 */
	public static void addAppKeys(List<ClientApp> apps) {
		Map<String, ClientApp> tem = new HashMap<String, ClientApp>();
		if(apps !=null ) {
			for(ClientApp app:apps) {
				tem.put(app.getAppKey(), app);
			}
		}
		ServerConfig.appKeys.putAll(tem);
	}
	/**
	 * 
	 * <p>Title:initWithConfig</p> 
	 * <p>Description: 根据配置文件初始化 </p> 
	 * @date 2017年7月20日 上午11:30:08
	 * @version 
	 * @return void
	 * @param configPath
	 * @throws IOException
	 */
	@Deprecated
	public  static void  initWithConfig(String configPath) throws IOException{
		Map<String,String> map = PropertyFileHandle.read(configPath);
		//System.out.println("启动配置信息：\n"+map);
		logger.info("启动配置信息：\n"+map);
		//设置appkey
		if(map.get("appKeys")!=null && !("").equals(map.get("appKeys"))){
			JSONArray json = JSONArray.fromObject(map.get("appKeys")); // 首先把字符串转成 JSONArray  对象
			if(json.size()>0){
			  for(int i=0;i<json.size();i++){
			    ClientApp clientApp = (ClientApp) JSONObject.toBean(json.getJSONObject(i), ClientApp.class);   
			    //System.out.println(clientApp) ;  // 日志输出
			    appKeys.put(clientApp.getAppKey(), clientApp);
			  }
			}
		}
		//设置白名单
		if(map.get("whiteList")!=null && !("").equals(map.get("whiteList"))){
			String[] str = map.get("whiteList").split(";");
			if(str!=null && str.length>0){
				whiteList.addAll(Arrays.asList(str));
			}
		}
		//设置黑名单
		if(map.get("blackList")!=null && !("").equals(map.get("blackList"))){
			String[] str = map.get("blackList").split(";");
			if(str!=null && str.length>0){
				blackList.addAll(Arrays.asList(str));
			}
		}
		//设置端口
		if(map.get("port")!=null && !("").equals(map.get("port"))){
			PORT = Integer.parseInt(map.get("port"));
		}
		//设置入站规则
		if(map.get("inboundRule")!=null && !("").equals(map.get("inboundRule"))){
			inboundRule = Integer.parseInt(map.get("inboundRule"))<=0?0:Integer.parseInt(map.get("inboundRule"))>3?3:Integer.parseInt(map.get("inboundRule"));
		}
		
	}
	/**
	 * <p>Title:init</p> 
	 * <p>Description: 默认初始化，加载 config.properties</p> 
	 * @date 2017年7月20日 上午11:32:03
	 * @version 
	 * @return void
	 * @throws IOException
	 */
	public static void  init() throws IOException{
		//initWithConfig("serverConfig.properties");
		init(null);
		isInit = true;
	}
	/**
	 * <p>Title:init</p> 
	 * <p>Description: 默认初始化，加载 config.properties</p> 
	 * @date 2017年8月2日 下午1:48:58
	 * @version 
	 * @return void
	 * @param configPaht  properties文件路径
	 * @throws IOException
	 */
	@Deprecated
	public static void  init(String configPaht) throws IOException{
		if(configPaht==null || ("").equals(configPaht));
			configPaht = "serverConfig.properties";
		initWithConfig(configPaht);
		isInit = true;
	}
	/**
	 * 
	 * <p>Title:init</p> 
	 * <p>Description: 初始化启动参数</p> 
	 * @date 2017年8月2日 上午10:16:03
	 * @version 
	 * @param ip 监听ip 默认本机 127.0.0.1
	 * @param port  默认 9166
	 * @param whiteList //白名单
	 * @param blackList
	 * @param inboundRule 入站规则  0  不做检查 1：白名单过滤  2 ：黑名单过滤
	 * @param appKeys @see Map<appkey,ClientApp>
	 */
	@Deprecated
	public static void  init(String ip,int port , List<String> whiteList,List<String> blackList,int inboundRule,Map<String,ClientApp> appKeys) {
		IP = ip==null?"127.0.0.1":ip;
		PORT = port<=0?9166:port;
		if(whiteList!=null && whiteList.size()>0){
			ServerConfig.whiteList = whiteList;
			//ServerConfig.whiteList.addAll(whiteList);
		}else{
			ServerConfig.whiteList.clear();
		}
		if(blackList!=null && blackList.size()>0){
			ServerConfig.blackList = blackList;
		}else{
			ServerConfig.blackList.clear();
		}
		ServerConfig.inboundRule = inboundRule;
		if(appKeys!=null){
			ServerConfig.appKeys = appKeys;
		}
		isInit = true;
	}
	/**
	 * 
	 * <p>Title:init</p> 
	 * <p>Description: 初始化启动参数</p> 
	 * @date 2017年8月2日 上午10:16:03
	 * @since 2.0.0 
	 * @param ip 监听ip 默认本机 127.0.0.1
	 * @param port  默认 9166
	 * @param whiteList //白名单
	 * @param blackList
	 * @param inboundRule 入站规则  0  不做检查 1：白名单过滤  2 ：黑名单过滤
	 * @param apps @see ClientApp[]
	 */
	public static void  init(String ip,int port , List<String> whiteList,List<String> blackList,int inboundRule,ClientApp...apps) {
		IP = ip==null?"127.0.0.1":ip;
		PORT = port<=0?9166:port;
		if(whiteList!=null && whiteList.size()>0){
			ServerConfig.whiteList = whiteList;
			//ServerConfig.whiteList.addAll(whiteList);
		}else{
			ServerConfig.whiteList.clear();
		}
		if(blackList!=null && blackList.size()>0){
			ServerConfig.blackList = blackList;
		}else{
			ServerConfig.blackList.clear();
		}
		ServerConfig.inboundRule = inboundRule;
		if(apps!=null && apps.length>0){
			Map<String,ClientApp> appKeys = new HashMap<String,ClientApp>();
			for(ClientApp app:apps) {
				appKeys.put(app.getAppKey(), app);
			}
			ServerConfig.appKeys = appKeys;
		}
		isInit = true;
	}
	/**
	 * 
	 * <p>Title:init</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月2日 下午1:41:07
	 * @version 
	 * @return void
	 * @param ip 监听ip 默认本机 127.0.0.1
	 * @param port 默认 9166
	 * @param appKeys @see Map<appkey,ClientApp>
	 */
	@Deprecated
	public static void init(String ip,int port,Map<String,ClientApp> appKeys){
		init(ip, port, null, null, 0,appKeys);
	}
	/**
	 * 
	 * <p>Title:init</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月2日 下午1:41:07
	 * @since 2.0.0
	 * @return void
	 * @param ip 监听ip 默认本机 127.0.0.1
	 * @param port 默认 9166
	 * @param apps @see ClientApp[]
	 */
	public static void init(String ip,int port,ClientApp...apps){
		init(ip, port, null, null, 0,apps);
	}
	/**
	 * @return the isInit
	 */
	public static boolean isInit() {
		return isInit;
	}
	/**
	 * @param isInit 如果手动调用每个set方法设置参数，请调用这个方法把参数设为true
	 */
	public static void setInit(boolean isInit) {
		ServerConfig.isInit = isInit;
	}
	/**
	 * 
	 * <p>Title:checkIp</p> 
	 * <p>Description: ip 检查 是否允许连接</p> 
	 * <p>根据入站规则检查{@see  inboundRule}</p>
	 * @date 2017年7月20日 下午1:26:23
	 * @version 
	 * @return boolean true：允许| false:不允许
	 * @param ip
	 * @return
	 */
	public static boolean checkIp(String ip){
		boolean isOK = true;
		if(ServerConfig.getInboundRule()!=0){ // 0 不做ip校验
			isOK = ServerConfig.getInboundRule()==1?false:true; //1：白名单校验 2： 黑名单校验
			if(isOK){ 
				//黑名单
				for (String WIP : ServerConfig.getBlackList()) {
					if (WIP.equals(ip)) {
						isOK = false;
						break;
					}
				}
			}else{
				for (String WIP : ServerConfig.getWhiteList()) {
					if (WIP.equals(ip)) {
						isOK = true;
						break;
					}
				}
			}
		}
		return isOK;
	}
	/**
	 * <p>Title:checkAppKey</p> 
	 * <p>Description: 检查 appkey是否合法</p> 
	 * @date 2017年7月20日 下午1:41:00
	 * @version 
	 * @return boolean
	 * @param appKey
	 * @return
	 */
	public static boolean checkAppKey(String appkey){
		boolean bln = false;
		if(null != appkey && !("").equals(appkey)){
			ClientApp clientApp = ServerConfig.getAppKeys().get(appkey);
			if(clientApp !=null){
				bln = clientApp.getStatus()>=0?true:false;
			}
		}
		return bln;
	}
	/**
	 * <p>Title:checkSign</p> 
	 * <p>Description: 登录签名认证，此时只根据appSecret去判断签名是否正确</p> 
	 * @date 2017年8月30日 下午3:35:32
	 * @return boolean
	 * @param appkey
	 * @param msg
	 * @return
	 * @since
	 */
	public static boolean loginCheckSign(String appkey,MiddleMsg msg){
		boolean bln = false;
		if(null != appkey && !("").equals(appkey)){
			ClientApp clientApp = ServerConfig.getAppKeys().get(appkey);
			if(clientApp !=null){
				bln = MsgSignTool.verifySign(clientApp.getAppSecret(), msg);
			}
		}
		return bln ;
	}
	public static String checkSign(String sessionId,MiddleMsg msg){
		String secret =null;
		boolean bln = false;
		if(null != sessionId && !("").equals(sessionId)){
			SecretManagement man = ServerGlobal.sessionWithAppKeys.get(sessionId);
			if(man !=null){
				bln = MsgSignTool.verifySign(man.getPresentSecret(), msg);
				if(bln){ //使用当前密码
					return man.getPresentSecret();
				}else{
					bln = MsgSignTool.verifySign(man.getPrevSecret(), msg);
					if(bln){ //使用之前的秘钥
						return man.getPrevSecret();
					}else{
						bln = MsgSignTool.verifySign(man.getNextSecret(), msg);
						if(bln){//使用下一个秘钥
							return man.getNextSecret();
						}
					}
				}
			}
			
		}
		return secret ;
	}
	
	
}
