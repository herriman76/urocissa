package com.sanyinggroup.corp.urocissa.client.init;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.client.api.future.SendFuture;
import com.sanyinggroup.corp.urocissa.client.api.future.SyncResponseMap;
import com.sanyinggroup.corp.urocissa.client.api.future.SyncSendFuture;
import com.sanyinggroup.corp.urocissa.client.api.handler.ClientHandler;
import com.sanyinggroup.corp.urocissa.client.api.handler.MsgHandler;
import com.sanyinggroup.corp.urocissa.client.api.handler.MsgHandlerCenter;
import com.sanyinggroup.corp.urocissa.client.codec.MessageDecoder;
import com.sanyinggroup.corp.urocissa.client.codec.MessageEncoder;
import com.sanyinggroup.corp.urocissa.client.codec.SecretManageCenter;
import com.sanyinggroup.corp.urocissa.client.event.AbstractEventHandler;
import com.sanyinggroup.corp.urocissa.client.event.EventUtil;
import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.client.model.MsgHead;
import com.sanyinggroup.corp.urocissa.client.model.ResultObject;
import com.sanyinggroup.corp.urocissa.client.util.SecretManagement;
import com.sanyinggroup.corp.urocissa.core.model.MessageType;
import com.sanyinggroup.corp.urocissa.core.util.MD5;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
/**
 * 客户端
 * <p>Package:com.sanyinggroup.corp.urocissa.client.init</p> 
 * <p>Title:Client</p> 
 * <p>Description: </p> 
 * @author lixiao
 * @date 2017年8月21日 上午11:37:17
 * @version 0.2
 */
public class Client extends MsgHandlerCenter{
	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	private Bootstrap b = new Bootstrap();
	private EventLoopGroup group = new NioEventLoopGroup();
	private StartConfig config;// 服务器配置项
	private volatile ChannelFuture future;
	private volatile Channel channel;
	private volatile boolean established = false; //已经建立连接的
	private volatile long  msgNum = 0L;
	private volatile String sessionId = "";
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);//心跳固定线程池
	private volatile ScheduledFuture<?> scheduleAtFixedRate;
	private volatile ResultObject result =  new ResultObject(-200, "连接服务器失败，请稍后再试...");
	private volatile boolean isConnecting =false;
	private Class<? extends AbstractEventHandler> eventHandlerClass = null; //事件处理类
	private volatile boolean loginSuccess = false; 
	private volatile boolean isShowdown = false; //是否关闭客户端
	
	public String getSessionId() {
		return sessionId;
	}
	/**
	 * <p>Title:getConfig</p> 
	 * <p>Description: 获取配置</p> 
	 * @date 2017年9月28日 上午10:10:46
	 * @return StartConfig
	 * @return
	 * @since
	 */
	public StartConfig getConfig(){
		return config;
	}
	/**
	 * 获取注册的事件处理类
	 * @author lixiao create at 2018年1月16日 下午4:50:30 
	 * @since 1.0.0
	 * @return
	 */
	public Class<? extends AbstractEventHandler> getEventHandlerClass(){
		return eventHandlerClass;
	}
	/**
	 * <p>Title:getResult</p> 
	 * <p>Description: 获取客户端也服务端的状态</p> 
	 * @date 2017年8月28日 上午9:39:40
	 * @return ResultObject
	 * @return
	 * @since
	 */
	public ResultObject getResult(){
		return result;
	}
	/**
	 * @param ip 远程server端ip
	 * @param port 远程server 端口
	 * @param appKey 服务端分配的appKey
	 * @param appSecret 服务端分配的appSecret
	 * @param eventHandlerClass
	 * @since 2.0.0
	 */
	protected Client(String ip, int port, String appKey, String appSecret,Class<? extends AbstractEventHandler>  eventHandlerClass) {
		super();
		if(eventHandlerClass!=null)
			this.eventHandlerClass = eventHandlerClass;
		config = new StartConfig(ip, port, appKey, appSecret);
		b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true);
		b.handler(new ChannelInitializer<SocketChannel>(){
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new MessageDecoder(2000*1024 * 1024, 4, 4)); //消息解码
				pipeline.addLast("MessageEncoder", new MessageEncoder());
				//pipeline.addLast("readTimeoutHandler", new ReadTimeoutHandler(120)); // 设置超时
				pipeline.addLast("handler", new ClientHandler());
			}
			
		});
		connect(); //建立连接
	}
	/**
	 * @param ip 远程server端ip
	 * @param port 远程server 端口
	 * @param appKey 服务端分配的appKey
	 * @param appSecret 服务端分配的appSecret
	 */
	protected Client(String ip, int port, String appKey, String appSecret) {
		this(ip, port, appKey, appSecret, null);
	}
	/**
	 * <p>Title:login</p> 
	 * <p>Description: 登录</p> 
	 * @date 2017年8月18日 下午4:46:49
	 * @version 
	 * @return boolean
	 * @throws Exception
	 */
	private synchronized ResultObject login() {
		if(loginSuccess) return result;
		MiddleMsg msg =  new MiddleMsg("_login", MessageType.LOGIN_REQ.value(), "");
		try {
			msg = sendMsgSync(msg);
		} catch (Exception e) {
			loginSuccess = false; //登录失败
			msg =null;
			result.setStatus(-1);
			result.setStatusMsg("error");
			result.setCause(e);
			closeFutrue();
		}
		if(msg==null || msg.getHeader() ==null){
			loginSuccess = false; //登录失败
			logger.error("通信登录失败...",result.getCause());
			EventUtil.loginError(result, eventHandlerClass);
			established = false; // 置空，重连
			closeFutrue();
			return result;
		}else if(msg.getHeader().getStatus() !=200 || isBlank(msg.getHeader().getSessionID())){
			loginSuccess = false; //登录失败
			logger.error("通信登录失败...状态码："+msg.getHeader().getStatus()+",状态信息："+msg.getHeader().getAttachment().get("statusMsg"));
			result.setStatus(msg.getHeader().getStatus());
			result.setStatusMsg(msg.getHeader().getAttachment().get("statusMsg").toString());
			result.setCause(null);
			closeFutrue();
			//established = false; // 置空，重连
			//heartBeatReq();
			EventUtil.loginError(result, eventHandlerClass);
			return result;
			//shutdown();
			//throw new IllegalAccessException(msg.getHeader().getStatus()+msg.getHeader().getAttachment().get("statusMsg").toString());
		}else {
			loginSuccess = true;
			sessionId =  msg.getHeader().getSessionID();
			logger.info("通信登录成功..."+msg.getBody());
			result.setStatus(200);
			result.setStatusMsg("通信登录成功");
			result.setCause(null);
			Map<String,Object> attachment = new  HashMap<String,Object>();
			attachment.put("sessionId", sessionId);
			attachment.put("channelId", future.channel().id().asShortText());
			result.setAttachment(attachment);
			// 登录成功后建立心跳
			if(scheduleAtFixedRate!=null && !scheduleAtFixedRate.isCancelled()){
				scheduleAtFixedRate.cancel(true);
			}
			SecretManageCenter.getSecretMan(config.getIp()+config.getPort()+config.getAppKey()).setNextSecret(msg.getBody().toString());
			//logger.debug(SecretManageCenter.getSecretMan(config.getIp()+config.getPort()+config.getAppKey()));
			heartBeatReq();
			ClientCenter.channelWithKey.put(future.channel().id().asShortText(), config.getIp()+config.getPort()+config.getAppKey());
			//登录成功  通知事件
			EventUtil.loginSuccess(result,eventHandlerClass);
			EventUtil.loginSuccess(result, this,eventHandlerClass);
			return result;
		}
	}
	/**
	 * <p>Title:heartBeatReq</p> 
	 * <p>Description: 心跳检测</p> 
	 * @date 2017年8月21日 下午5:09:33
	 * @return void
	 */
	private synchronized void heartBeatReq(){
		 scheduleAtFixedRate = executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if(established){ // 是否建立连接并登陆的
					//专门留作心跳处理器去处理
					MiddleMsg msg =  new MiddleMsg("_heartBeatReq", MessageType.HEARTBEAT_REQ.value(), null);
					try {
						sendMsg(msg, new MsgHandler() {
							@Override
							public void callback(MiddleMsg msg) {
								if(msg.getHeader().getStatus()==200) { //心跳正常
									if( msg.getBody()!=null && msg.getBody().toString().length()==32){
										logger.debug("设置下一个秘钥："+msg.getBody().toString());
										SecretManageCenter.getSecretMan(config.getIp()+config.getPort()+config.getAppKey()).setNextSecret(msg.getBody().toString());
									}
								}else { // 心跳非正常
									closeFutureAndCancleScheduleAndReconnect();
									logger.debug("心跳信息："+(msg.getHeader().getStatus()==200?"正常":"非正常"));
								}
							}
						});
					} catch (Exception e) {
						logger.error("心跳异常：",e);
						closeFutureAndCancleScheduleAndReconnect();
					}
				}else{
					logger.debug("心跳异常：");
					closeFutureAndCancleScheduleAndReconnect();
				}
			}
		}, 0, 20, TimeUnit.SECONDS); // 20秒发送一次心跳
		
	}
	/**
	 * 关闭future 同时取消心跳，重新连接
	 * @author lixiao create at 2018年5月18日 下午3:16:12 
	 * @since 2.0.3
	 */
	private void closeFutureAndCancleScheduleAndReconnect() {
		closeFutureAndCancleSchedule();
		connect();
	}
	/**
	 * 关闭future 同时取消心跳
	 * @author lixiao create at 2018年5月18日 下午3:16:12 
	 * @since 2.0.3
	 */
	private void closeFutureAndCancleSchedule() {
		closeFutrue();
		scheduleAtFixedRate.cancel(true);
	}
	/**
	 * 关闭future
	 * @author lixiao create at 2018年5月18日 下午3:16:12 
	 * @since 2.0.3
	 */
	private void closeFutrue() {
		try {
			if(future!=null){
				future.channel().disconnect();
				future.channel().close();
				future.cancel(true);
				future = null;
				channel =null;
				sessionId="";
			}
			established = false; //断开建立的连接
			loginSuccess = false; //登录失败
		} catch (Exception e) {
			logger.error("关闭future异常：",e);
		}
	}
	/**
	 * <p>Title:connect</p> 
	 * <p>Description: 获取连接</p> 
	 * @date 2017年8月17日 下午6:28:34
	 * @version 
	 * @return boolean
	 */
	private synchronized boolean connect(){
		if(future!=null && future.isSuccess() && channel!=null && channel.isWritable()==true){
			return true;
		}
		
		boolean bln =establishConnect();
		if(bln) {
			result = ResultObject.getSuccessObject();
			result.setStatusMsg("通信连接成功");
		}
		if(login().getStatus()!=200){ // 登录失败
			bln  = false;
		}else {
			 // 登录失败   事件已在login方法内中处理
		}
		if(bln){ // 连接成功
			
			
		}else{ // 连接失败
			if(!isConnecting){ // 不在重连
				isConnecting = true;
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						int i=0;
						while(!established && !isShowdown){
							i++;
							try {
								logger.debug("第"+i+"次,尝试重新连接中...");
								established = establishConnect();
								if(established) {
									result = ResultObject.getSuccessObject();
									result.setStatusMsg("通信连接成功");
								}
								if(login().getStatus()!=200){ // 登录失败
									established  = false;
								}else {
									 // 登录失败   事件已在login方法内中处理
								}
								TimeUnit.SECONDS.sleep(2*i<=120?2*i:120);//30次之后每3分钟练一次
							} catch (InterruptedException e) {
								logger.error("InterruptedException");
							}
						} // 连接成功
						if(!isShowdown) { // 
							logger.info("重新连接成功...");
							//result = ResultObject.getSuccessObject();
							isConnecting = false;
							login();
						}else {
							logger.info("终止自动连接...");
						}
					}
				});
				thread.setDaemon(true);
				thread.start();
				
			}
		}
		
		
		return bln;
	}
	/**
	 * <p>Title:establishConnect</p> 
	 * <p>Description: 建立连接</p> 
	 * @date 2017年8月23日 下午3:39:53
	 * @version 
	 * @return boolean
	 */
	private synchronized boolean establishConnect(){
		if(established) return true;
		try {
			//在连接之前重新设置appsecret
			SecretManagement man  = new SecretManagement(config.getAppKey(),config.getAppSecret());
			SecretManageCenter.setSecretMan(config.getIp()+config.getPort()+config.getAppKey(), man); //设置初始秘钥
			logger.debug("设置初始秘钥："+config.getIp()+config.getPort()+config.getAppKey()+":"+man);
			b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000); //设置连接超时时间  5秒   ，5秒连接不上自动断开
			ChannelFuture future_t = b.connect(new InetSocketAddress(config.getIp(), config.getPort())).sync();
			
					/*.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					if(future.isSuccess()){
						logger.debug("连接成功");
					}else{
						logger.debug("连接失败。。。");
					}
				}
			});*/
			if(future_t!=null && future_t.isSuccess()){
				future = future_t;
				established =true;
				return true;
			}else{
				established =false;
				result.setStatus(-200);
				result.setStatusMsg(config.getIp()+":"+config.getPort()+"连接失败,等待自动重新连接...");
			}
		} catch (Exception e) {
			//future.cancel(false);
			established =false;
			result.setStatus(-1);
			result.setStatusMsg(config.getIp()+":"+config.getPort()+"连接失败,等待自动重新连接...");
			result.setCause(e);
			logger.error(config.getIp()+":"+config.getPort()+"连接失败,等待自动重新连接...");
		}
		//通知事件处理 -- 服务器连接失败
		EventUtil.connectFail(result, eventHandlerClass);
		return false;
	}
	
	/**
	 * <p>Title:sendMsg</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月18日 上午10:19:23
	 * @version 
	 * @return Object
	 * @param msg
	 * @param handler
	 * @param sync 是否同步， 同步为true
	 * @throws Exception 
	 */
	protected synchronized ResultObject sendMsg(MiddleMsg msg,MsgHandler handler,boolean sync) {
		try {
			Thread.sleep(5); // Don't ask me why, did I do that
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
		if(msg==null || msg.getHeader()==null){
			return new ResultObject(400, "请求头错误",new NullPointerException("MiddleMsg msg不能为null，且消息头不能为null"));
		}else {
			if(!established && msg.getHeader().getType()!=MessageType.LOGIN_REQ.value()){
				ResultObject res = result;
				connect(); //重新连接
				if(res!=null && res.getStatus()!=200){
					return res;
				}
				return new ResultObject(-200, "服务器断开连接，请稍后再试");
			}
			msg.getHeader().setSessionID(sessionId);
			if(handler!=null && !sync){//非同步
				String handlerKey = MD5.toMD5(config.getIp()+config.getPort()+config.getAppKey()+msg.getHeader().getAction());
				if(MsgHandlerCenter.getMsgHandler(handlerKey)==null){ //判断handler注册中心是否已经注册
					MsgHandlerCenter.regist(handlerKey, handler); //handler注册到注册中心
				}
				msg.getHeader().getAttachment().put("v02handerkey", handlerKey);
			}else if(sync){// 同步
				msg.getHeader().getAttachment().put("v02handerkey", "$synchandlerkey$");
			}
			msg.getHeader().setAppKey(config==null?"":config.getAppKey());
			boolean bln = send(msg,handler,sync);
			if(bln){
				if(msg.getHeader().getType() ==MessageType.CLIENT_REQ.value()){
					msgNum++;
				}
				ResultObject res = ResultObject.getSuccessObject();
				Map<String,Object> attachment = new HashMap<String, Object>(1);
				attachment.put("msgId", msg.getHeader().getMsgId());
				res.setAttachment(attachment);
				return res; //正常返回success
			}else{
				logger.error("消息发送失败");
				if(result!=null && result.getStatus()!=200) {
					return result;
				}
				ResultObject res =ResultObject.getErrorObject();
				res.setStatusMsg("消息发送失败");
				return res;
			}
		}
	}
	/**
	 * <p>Title:send</p> 
	 * <p>Description: 是否发送成功</p> 
	 * @date 2017年8月24日 下午5:20:16
	 * @version 
	 * @return boolean
	 * @param msg
	 * @return
	 */
	private boolean send(final MiddleMsg msg,final MsgHandler handler,final boolean sync){
		if(channel ==null && future!=null)
			channel = future.channel();
//		logger.debug(channel.isOpen()+"");
//		logger.debug(channel.isActive()+"");
//		logger.debug(channel.isActive()+"");
		if(channel!=null && channel.isOpen() && channel.isActive() && channel.isWritable()){
			channel.writeAndFlush(msg).addListener(new ChannelFutureListener(){
				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					if(future.isSuccess()){
						//logger.debug("sendMsg->Success");
					}else{ //
						established = false;
						logger.debug("sendMsg->failed:\n"+future.cause());
						ResultObject result  = new ResultObject(-200, "消息发送失败");
						result.setCause(future.cause());
						if(sync){
							SendFuture<MiddleMsg> sendFuture = SyncResponseMap.syncKey.get(msg.getHeader().getMsgId());
							MiddleMsg res = msg;
							res.getHeader().setStatus(-1); //发送失败
							res.getHeader().getAttachment().put("statusMsg", "发送消息失败");
							res.setBody(null);
							sendFuture.setResponse(res);
							logger.error("同步消息发送失败");
							// 同步的就不管他了，同步消息处理的时候肯定会超时，那时候会有处理
						}else{ 
							//reSendMsg(msg, handler, sync); //异步的再发一次试试，不行就算了
							logger.error("消息发送失败");
						}
						//通知消息发送失败回调事件
						EventUtil.msgSendFail(msg, result, eventHandlerClass);
					}
				}
				
			});
			return true;		
		}else{ // channel 不可用
			established = false;
			closeFutrue();
			//通知消息发送失败回调事件
			if(result!=null && result.getStatus()==200) {
				result.setStatus(-200);
				result.setStatusMsg("未连接到服务器,等待自动连接...");
			}
			EventUtil.msgSendFail(msg, result, eventHandlerClass);
			return false;
		}
	}
	/**
	 * <p>Title:reSendMsg</p> 
	 * <p>Description: 发送失败重新建立连接</p> 
	 * @date 2017年8月21日 下午5:11:08
	 * @version 
	 * @return String
	 * @param msg
	 * @param handler
	 * @param sync
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private synchronized ResultObject reSendMsg(MiddleMsg msg,MsgHandler handler,boolean sync){
		if(!established){
			if(future!=null){
				future.channel().disconnect();
				channel =null;
				future = null;
			}
			//executor.shutdownNow(); //断开心跳连接，等待重新连接
			if(scheduleAtFixedRate!=null && !scheduleAtFixedRate.isCancelled() )
			scheduleAtFixedRate.cancel(true);
			if(connect()){
				try {
					if(login().getStatus()==200){
						heartBeatReq();//建立心跳
						logger.debug("重新登录成功...");
						channel = future.channel();
						return sendMsg ( msg, handler, sync) ;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
			return sendMsg ( msg, handler, sync) ;
		}
		
		return new ResultObject(-1, "发送失败");
	}
	/**
	 * <p>Title:sendMsg</p> 
	 * <p>Description: 发送异步消息</p> 
	 * @date 2017年8月18日 下午5:45:40
	 * @version 
	 * @return ResultObject
	 * @param msg
	 * @param handler
	 * @throws Exception
	 */
	public ResultObject sendMsg(MiddleMsg msg,MsgHandler handler) {
		return sendMsg(msg, handler, false);
	}
	/**
	 * <p>Title:sendMsgSync</p> 
	 * <p>Description: 发送同步信息</p> 
	 * @date 2017年8月18日 下午5:51:39
	 * @version 
	 * @return MiddleMsg
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public  MiddleMsg sendMsgSync(MiddleMsg msg) throws Exception{
		if(msg==null || msg.getHeader()==null || msg.getHeader().getAction() ==null){
			logger.error("msg及msg头不能为空");
			MiddleMsg err = new MiddleMsg();
			MsgHead header = new MsgHead();
			header.setStatus(-200);
			header.getAttachment().put("statusMsg", "msg及msg头中action不能为空");
			err.setHeader(header);
			return  err;
		}
		if(isBlank(msg.getHeader().getMsgId())){
			msg.getHeader().setMsgId(UUID.randomUUID().toString());
		}
		String msgId=msg.getMsgHead().getMsgId();
		SendFuture<MiddleMsg> response = new SyncSendFuture(msgId);
		SyncResponseMap.syncKey.put(msgId, response);
		//发送同步消息
		ResultObject res = sendMsg(msg, null, true);
		if(res.getStatus()==200){ //调用发送成功
			response =  SyncResponseMap.syncKey.get(msgId);
			MiddleMsg resp= response.getResponse();//获取同步消息时会等待
			if(response.isTimeout()){ 
				msg.getHeader().setStatus(-202);
				msg.getHeader().getAttachment().put("statusMsg", "同步消息请求超时");
				msg.setBody(null);
				logger.error("同步消息请求超时");
			}else if(resp != null){
				msg = resp;
			}
		}else{ //调用失败
			msg.getHeader().setStatus(res.getStatus());
			msg.getHeader().getAttachment().put("statusMsg", res.getStatusMsg());
			msg.setBody(null);
		}
		SyncResponseMap.syncKey.remove(msgId);
		return msg;
	}
	
	public  MiddleMsg sendMsgSync(String action,MiddleMsg msg) throws Exception{
		msg.getHeader().setAction(action);
		return sendMsgSync(msg);
	}
	public static boolean isBlank(String str){
		if(str==null || ("").equals(str.trim())){
			return true;
		}
		return false;
	}
	/**
	 * <p>Title:getMsgNum</p> 
	 * <p>Description: 获取发送消息总数</p> 
	 * @date 2017年8月21日 下午5:13:11
	 * @version 
	 * @return long
	 * @return
	 */
	public long getMsgNum(){
		return msgNum;
	}
	/**
	 * <p>Title:shutdown</p> 
	 * <p>Description: 关闭客户端的线程</p> 
	 * @date 2017年8月29日 下午3:46:30
	 * @return void
	 * @since
	 */
	protected void shutdown() {
		isShowdown  =true;
		try {
			Set<String> keySet = SyncResponseMap.syncKey.keySet();
			for(String key:keySet){
				SyncResponseMap.syncKey.get(key).setResponse(null);
			}
			// 停止心跳
			if(scheduleAtFixedRate!=null && !scheduleAtFixedRate.isCancelled() )
				scheduleAtFixedRate.cancel(true);
			executor.shutdownNow();
			established = false;
			String key = ClientCenter.channelWithKey.get(channel.id().asShortText());
			if(key!=null)
				ClientCenter.channelWithKey.remove(key);
			if(future!=null){
				future.channel().disconnect();
				channel =null;
				future = null;
			}
			if(channel!=null){
				channel.close();
				channel.closeFuture();
				channel = null;
			}
			
			//established = true; //为了防止线程一直跑， 把这个置为true
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		group.shutdownGracefully();
		logger.info("客户端优雅退出...");
		
		
	}
	@Override
	public String toString() {
		return "Client [config=" + config + ", established=" + established + ", msgNum=" + msgNum + ", sessionId="
				+ sessionId + ", result=" + result + ", isConnecting=" + isConnecting + ", eventHandlerClass="
				+ eventHandlerClass + ", isShowdown=" + isShowdown + "]";
	}
	
	
	/*public static void main(String[] args) {
		try {
			ResourceLeakDetector.setLevel(Level.ADVANCED);
			//final Client client = ClientCenter.getAClient("127.0.0.1", 9166, "auth001", "appSecret0101003.1");
			//final Client client = ClientCenter.getAClient("192.168.6.215", 9166, "99zhaiquan.main", "99zhaiquan.main");
			final Client client = ClientCenter.getAClient("127.0.0.1", 9166, "123", "12345678",EventHandlerTest.class);
			//客户端，修改PushReceiver接口中handleReceivedMsg的返回值为MiddleMsg
			PushReceiverCenter.registReceiver("aaa", new PushReceiver() {
				
				@Override
				public MiddleMsg handleReceivedMsg(MiddleMsg msg) {
					System.out.println("->====处理服务器的推送消息："+msg);
					msg.setBody("客户端接收到服务器消息");
					return msg;
				}
			});
			
			if(client.getResult().getStatus()==200){
				// do sometiong...
			}
			System.out.println(client);
			System.err.println("sessionid："+client.sessionId);
		   // int ji = 0;
		    for(int i=0;i<1;i++){
		    	//BodyTest t = new BodyTest();
		    	//ji = i+1;
				MiddleMsg msg = new MiddleMsg("test1", "dddd"+i);
		    	//MiddleMsg msg = new MiddleMsg("test2", t.readFile());
				TimeUnit.SECONDS.sleep(3);
				//System.out.println(client.sendMsgSync(msg));
				client.sendMsg(msg, new MsgHandler() {
					@Override
					public void callback(MiddleMsg msg) {
						System.out.println("--------------------");
						System.out.println(client.msgNum);
						System.out.println(msg);
						System.out.println("---------------------");
					}
				});
				
		  }
			//Thread.sleep(4000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("主线程异常",e);
		}
	}
	
	*/
}
