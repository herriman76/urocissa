package com.sanyinggroup.corp.urocissa.server;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.core.util.SecretManagement;
import com.sanyinggroup.corp.urocissa.server.api.service.MsgServiceHandler;
import com.sanyinggroup.corp.urocissa.server.api.service.MsgServiceHandlerRegister;


/**
 * server 端全局统计
 * <p>Package:com.sanyinggroup.communication.server</p> 
 * <p>Title:ServerGlobal</p> 
 * <p>Description: 端全局统计 </p> 
 * @author lixiao
 * @date 2017年8月2日 上午10:25:24
 * @version 0.1
 */
public class ServerGlobal {
	private static final Logger logger =LoggerFactory.getLogger(ServerGlobal.class);
	private static volatile int clientNum=0;// 客户端数量
	private static volatile long msgNum=0L; //处理成功总消息数
	private static volatile long failedNum=0l; //处理失败的消息数
	private static volatile Map<String ,Long> sessionMsgNum = new Hashtable<String, Long>();//每个客户端发送消息数
	public static volatile Map<String, SecretManagement> sessionWithAppKeys = new Hashtable<String, SecretManagement>();//session会话head
	/**
	 * @since 2.0.0 
	 * key channelId
	 * value sessionId
	 */
	public static volatile Map<String, String > channelId2SessionId = new Hashtable<String, String>();
	
	public static final String desKey="!QA@WS#ED";
	static{
		Thread daemonThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					String sysInfo="\n----------------------系统信息Begin---------------------------------";
					sysInfo +=("\n当前客户端连接数："+clientNum+" | "+ "当前消息总数："+msgNum+" | "+ "处理失败总数："+failedNum);
					Set<String> sessionids = sessionWithAppKeys.keySet();
					//logger.debug(sessionids);
					for(String sessionid:sessionids){
						//logger.debug(new Date().getTime() - sessionWithAppKeys.get(sessionid).getTimestamp());
						//logger.info(sessionWithAppKeys.get(sessionid));
						if((new Date().getTime() - sessionWithAppKeys.get(sessionid).getTimestamp())>60000){ //心跳>60秒 
							sysInfo +=("\n客户端："+sessionWithAppKeys.get(sessionid)+"心跳异常，断开连接，总处理消息："+sessionMsgNum.get(sessionid)+"条");
							sessionWithAppKeys.remove(sessionid);
							sessionMsgNum.remove(sessionid);
							clientNum-- ;
							sysInfo +=("\n当前客户端连接数："+sessionWithAppKeys.size()+" | "+ "当前消息总数："+msgNum+" | "+ "处理失败总数："+failedNum);
						}else{
							System.out.println("\nsessionId:"+ sessionid+":"+ (sessionMsgNum.get(sessionid)==null?0:sessionMsgNum.get(sessionid))+" | ");
							sysInfo +=("\nsessionId:"+ sessionid+":"+ (sessionMsgNum.get(sessionid)==null?0:sessionMsgNum.get(sessionid))+" | ");
						}
					}
					//Map<String, MsgServiceHandler> hs = MsgServiceHandlerRegister.getRegister().getAllRegistedHandlers();
					Map<String, Class<MsgServiceHandler>> hs = MsgServiceHandlerRegister.getRegister().getAllRegistedHandlersWithClass();
					if(logger.isDebugEnabled()){ //debug 开启的情况下去打印日志
						Set<String> keySet = hs.keySet();
						sysInfo +="\n服务端注册信息\n";
						for(String key:keySet){
							sysInfo += "action="+key +" : "+hs.get(key).getName()+"\n";
						}
					}
					logger.debug(sysInfo+"----------------------系统信息End---------------------------------");
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						//e.printStackTrace();
						logger.error("打印服务端系统信息失败：", e);
					}
				}
			}
		}); // 设置为守护线程
		daemonThread.setDaemon(true);
		daemonThread.start();
	}
	/**
	 * 
	 * <p>Title:getClientNum</p> 
	 * <p>Description: 获取当前连接客户端数量</p> 
	 * @date 2017年7月20日 下午5:11:47
	 * @version 
	 * @return void
	 */
	public static int getClientNum(){
		if(clientNum<0) {
			clientNum = 0;
		}
		return clientNum;
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
		return msgNum;
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
		return sessionMsgNum.get(sessionId)==null?0:sessionMsgNum.get(sessionId);
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
		return failedNum;
	}
	/**
	 * 
	 * <p>Title:clientConnected</p> 
	 * <p>Description: 客户端连接上</p> 
	 * @date 2017年7月20日 下午5:14:02
	 * @version 
	 * @return int
	 * @return
	 */
	public static void clientConnected(){
		 clientNum++;
	}
	/**
	 * 
	 * <p>Title:clientDisConnected</p> 
	 * <p>Description: 客户端断开连接</p> 
	 * @date 2017年7月20日 下午5:15:54
	 * @version 
	 * @return int
	 * @return
	 */
	public static void clientDisConnected(){
		 clientNum--;
	}
	/**
	 * 
	 * <p>Title:receiveMsg</p> 
	 * <p>Description: 获取到消息总数</p> 
	 * @date 2017年7月20日 下午5:18:55
	 * @version 
	 * @return Long
	 * @return
	 */
	public static long receiveMsg(String sessionId){
		msgNum++;
		synchronized (sessionMsgNum) {
			long a=0L;
			if(sessionMsgNum.get(sessionId)==null || sessionMsgNum.get(sessionId)==0 ){
				sessionMsgNum.put(sessionId, 1L);
				return 1l;
			}else{
				a =sessionMsgNum.get(sessionId)+1;
				sessionMsgNum.put(sessionId, a);
			}
			return a;
		}
	}
	/**
	 * 
	 * <p>Title:handleFalse</p> 
	 * <p>Description: 消息处理失败</p> 
	 * @date 2017年7月20日 下午5:22:44
	 * @version 
	 * @return void
	 */
	public static void handleFalse(){
		failedNum++;
	}
	
}
