package com.sanyinggroup.corp.urocissa.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sanyinggroup.corp.urocissa.server.api.info.ClientApp;
import com.sanyinggroup.corp.urocissa.server.api.service.MsgServiceHandler;



public class Server {
	private  int server_port;
	private List<ClientApp> clients = new ArrayList<ClientApp>();
	private List<String> whiteList ;
	//黑名单
	private List<String> blackList ;
	//入站规则  0 不做检查 1：白名单  2 ：黑名单
	private static int inboundRule = 0;
	
	private Map<String,MsgServiceHandler> handlers;
	/**
	 * @return the server_port
	 */
	public  int getServer_port() {
		return server_port;
	}
	/**
	 * @param server_port the server_port to set
	 */
	public  void setServer_port(int server_port) {
		this.server_port = server_port;
	}
	/**
	 * @return the clients
	 */
	public List<ClientApp> getClients() {
		return clients;
	}
	/**
	 * @param clients the clients to set
	 */
	public void setClients(List<ClientApp> clients) {
		this.clients = clients;
	}
	/**
	 * @return the whiteList
	 */
	public List<String> getWhiteList() {
		return whiteList;
	}
	/**
	 * @param whiteList the whiteList to set
	 */
	public void setWhiteList(List<String> whiteList) {
		this.whiteList = whiteList;
	}
	/**
	 * @return the blackList
	 */
	public List<String> getBlackList() {
		return blackList;
	}
	/**
	 * @param blackList the blackList to set
	 */
	public void setBlackList(List<String> blackList) {
		this.blackList = blackList;
	}
	
	
	/**
	 * @return the inboundRule
	 */
	public static int getInboundRule() {
		return inboundRule;
	}
	/**
	 * @param inboundRule the inboundRule to set
	 */
	public static void setInboundRule(int inboundRule) {
		Server.inboundRule = inboundRule;
	}
	
	/**
	 * @return the handlers
	 */
	public Map<String, MsgServiceHandler> getHandlers() {
		return handlers;
	}
	/**
	 * @param handlers the handlers to set
	 */
	public void setHandlers(Map<String, MsgServiceHandler> handlers) {
		this.handlers = handlers;
	}
	/**
	 * <p>Title:init</p> 
	 * <p>Description: 服务端初始化 </p> 
	 * @date 2017年10月27日 下午5:58:10
	 * @return void
	 * @param server
	 * @throws Exception
	 * @since
	 */
	public static  boolean init(Server server)  {
		if(server ==null || 0 > server.getServer_port() || server.getServer_port()>65535){
			throw new IllegalArgumentException("服务器启动参数不正确，或端口不在指定范围内，指定范围为0 ~ 65535");
		}else{
			return ServerInit.init(server.getServer_port(),server.getClients(),server.getBlackList(),
					server.getWhiteList(),inboundRule,server.handlers);
		}
	}
	/**
	 * <p>Title:init</p> 
	 * <p>Description: server 端默认初始化  默认启动端口9166</p> 
	 * @date 2017年11月2日 下午1:57:43
	 * @return boolean
	 * @return
	 * @since
	 */
	public static  boolean init()  {
		Server server = new Server();
		server.setServer_port(9166);
		return init(server);
	}
	
	//public static void addClient(Client)
	/**
	 * 
	 * <p>Title:getClientNum</p> 
	 * <p>Description: 获取当前连接客户端数量</p> 
	 * @date 2017年7月20日 下午5:11:47
	 * @version 
	 * @return void
	 */
	public static int getClientNum(){
		return ServerGlobal.getClientNum();
	}
	/**
	 * <p>Title:getMsgNum</p> 
	 * <p>Description:获取处理消息总数</p> 
	 * @date 2017年7月20日 下午5:25:29
	 * @version 
	 * @return long
	 * @return
	 */
	public static long getMsgNum(){
		return ServerGlobal.getMsgNum();
	}
	/**
	 * 
	 * <p>Title:getMsgNum</p> 
	 * <p>Description:  根据sessionId取消息数</p> 
	 * @date 2017年7月20日 下午6:16:57
	 * @version 
	 * @return long
	 * @param sessionId
	 * @return
	 */
	public static long getMsgNum(String sessionId){
		return ServerGlobal.getMsgNum(sessionId);
	}
	/**
	 * <p>Title:getFailedNum</p> 
	 * <p>Description: 获取处理消息失败总数</p> 
	 * @date 2017年7月20日 下午5:25:03
	 * @version 
	 * @return long
	 * @return
	 */
	public static long getFailedNum(){
		return ServerGlobal.getFailedNum();
	}
	
	
	
	
	
	
}
