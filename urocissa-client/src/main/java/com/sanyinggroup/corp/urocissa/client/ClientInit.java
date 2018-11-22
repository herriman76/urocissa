package com.sanyinggroup.corp.urocissa.client;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.client.api.channel.ClientChannelInitializer;
import com.sanyinggroup.corp.urocissa.client.api.future.SendFuture;
import com.sanyinggroup.corp.urocissa.client.api.future.SyncResponseMap;
import com.sanyinggroup.corp.urocissa.client.api.future.SyncSendFuture;
import com.sanyinggroup.corp.urocissa.client.api.handler.MsgHandler;
import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.core.model.MessageType;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 
 * <p>
 * Package:com.sanyinggroup.communication.client
 * </p>
 * <p>
 * Title:ClientInit 
 * </p>
 * <p>
 * Description:客户端初始化
 * </p>
 * @author lixiao
 * @date 2017年7月17日 下午5:55:51
 * @version
 */
@Deprecated
public class ClientInit {

	Bootstrap b = new Bootstrap();
	EventLoopGroup group = new NioEventLoopGroup();
	private static final Logger logger = LoggerFactory.getLogger(ClientInit.class.getName());
	private static ClientInit client;
	private long msgNum = -1L;
	private String sessionID = "";
	private int waitTime=25;
	private static Map<String,Channel> channelPool = new HashMap<String,Channel>();
	private ClientInit() throws Exception {
		super();
		// 为了防止老版本中和新版本有冲突，从1.0.0开始注释掉
		b.group(group).channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true);
		MiddleMsg msg = new MiddleMsg("", MessageType.LOGIN_REQ.value(), "");
		connect(msg, new MsgHandler() {
			@Override
			public void callback(MiddleMsg msg) {
				if(msg!=null){
					sessionID = msg.getHeader().getSessionID();
					logger.info("sessionid:"+sessionID);
					ClientConfig.serverStatus=1;
				}
			}
		});
		
	}
	/**
	 * <p>Title:shutdown</p> 
	 * <p>Description: 关闭客户端</p> 
	 * @date 2017年8月16日 下午6:05:43
	 * @version 
	 * @return void
	 */
	public void shutdown(){
		channelPool = null;
		group.shutdownGracefully();
		client = null;
	}
	
	public Channel getChannel(String host,int port,String action,MsgHandler msgHandler) throws Exception{
		Channel channel =null;
		channel = channelPool.get(action);
		/*synchronized (channelPool) {
		}*/
		if(channel == null || !channel.isWritable()){
			try {
				b.handler(new ClientChannelInitializer(msgHandler));
				ChannelFuture future = b.connect(
						new InetSocketAddress(host, port)).sync();
				channel = future.channel();
				channelPool.put(action, channel);
			}finally{
				if(channel ==null){
					logger.debug("=====连接失败，重新连接=========");
					TimeUnit.SECONDS.sleep(3);
					return getChannel( host, port, action, msgHandler);
				}
				
			}
		}else{
			logger.debug("=======channle 复用=======");
		}
		
		return channel;
	}
	/**
	 * <p>Title:connect</p> 
	 * <p>Description: </p> 
	 * @date 2017年7月18日 下午3:21:58
	 * @version 
	 * @return void
	 * @param port
	 * @param host
	 * @param msg
	 * @param msgHandler 收到消息回复后的回调处理 类
	 * @throws Exception
	 */
	public void connect(int port, String host,MiddleMsg msg,MsgHandler msgHandler) throws Exception {
		try {
			b.handler(new ClientChannelInitializer(msgHandler));
			ChannelFuture future = b.connect(new InetSocketAddress(host, port))
					.sync();
			Channel channel = future.channel();
			if (msg != null) {
				msg.getMsgHead().setSessionID(sessionID);
				channel.writeAndFlush(msg);
				msgNum++;
			}
			
			b.option(ChannelOption.SO_KEEPALIVE, true);
			channel.closeFuture().sync();
			
		} finally {
			 //connect(port, host,msg, msgHandler);
			 logger.debug("-------------------");
			//group.shutdownGracefully();
		}
	}
	public void connect(String host,int port, MiddleMsg msg ,MsgHandler msgHandler) throws Exception{
		if (msg != null) {
			final String msgId = msg.getHeader().getMsgId();
			Channel channel = getChannel(host, port, msg.getHeader().getAction(), msgHandler);
			msg.getMsgHead().setSessionID(sessionID);
			channel.writeAndFlush(msg).addListener(new ChannelFutureListener(){
				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					if(!future.isSuccess()){
						SyncResponseMap.syncKey.remove(msgId);
					}
				}
				
			});
			msgNum++;
			//b.option(ChannelOption.SO_KEEPALIVE, true);
			
			/*channel.closeFuture().sync();
			channelPool.remove(msg.getHeader().getAction());
			logger.log(Level.INFO, "channel连接断开");*/
		}
	}
	public void connect(MiddleMsg msg,MsgHandler msgHandler) throws Exception{
		//connect(ClientConfig.getPort(), ClientConfig.getIp(), msg, msgHandler);
		connect(ClientConfig.getIp(), ClientConfig.getPort(), msg, msgHandler);
	}
	/**
	 * 客户端初始化
	 * <p>Title:init</p> 
	 * <p>Description: </p> 
	 * <p>默认加载 classpath 中 "clientConfig.properties"文件作为初始化文件</p>
	 * @date 2017年7月18日 下午3:25:31
	 * @version 
	 * @return ClientInit
	 * @return
	 * @throws Exception 
	 */
	@Deprecated
	public static ClientInit init() throws Exception {
		if(ClientConfig.serverStatus<0){
			client =null; //重置所有的
			channelPool.clear();
		}
		if(client==null){
			if(!ClientConfig.isInit()){
				ClientConfig.init("");
			}
			client = new ClientInit();
			while(client.waitTime>0 && ("").equals(client.sessionID)){
				Thread.sleep(1000);
				client.waitTime--;
			}
			if(client.sessionID==""){
				client.group.shutdownGracefully();
				//logger.info( "登录失败");//
				throw new Exception("登录失败!");
			}
		}
		return client ;
	}
	/**
	 * 
	 * <p>Title:init</p> 
	 * <p>Description: 客户端初始化</p> 
	 * @date 2017年8月2日 下午1:55:17
	 * @version 
	 * @return ClientInit
	 * @param appKey
	 * @param appSecret
	 * @param remotePort 远程端口
	 * @param remoteIp  远程ip
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public static ClientInit init(String appKey,String appSecret,int remotePort,String remoteIp) throws Exception{
		ClientConfig.init(appKey, appSecret, remotePort, remoteIp);
		return init();
	}
	/**
	 * <p>Title:sendMsg</p> 
	 * <p>Description: 发送消息  </p> 
	 * <p>如果在这个方法之前不初始化init参数，或者不初始化ClientConifg ，直接发送消息  ，
	 *  需在classpath中添加"clientConfig.properties"文件作为初始化文件</p> 
	 * @date 2017年7月18日 下午3:24:50
	 * @version 
	 * @return String : 返回消息 msgId
	 * @param msg
	 * @param msgHandler 消息处理类
	 * @throws Exception
	 */
	@Deprecated
	public static String sendMsg(MiddleMsg msg,MsgHandler msgHandler) throws Exception{
		if(msg==null){
			return null;
		}
		ClientInit.init().connect(msg,msgHandler);
		return msg.getHeader().getMsgId();
	}
	/**
	 * 
	 * <p>Title:sendMsgSync</p> 
	 * <p>Description: 同步发送消息</p> 
	 * @date 2017年8月11日 上午10:03:33
	 * @version 
	 * @return MiddleMsg
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public static MiddleMsg sendMsgSync(MiddleMsg msg) throws Exception{
		if(msg==null){
			throw new NullPointerException("msg");
		}
		if(isBlank(msg.getHeader().getMsgId())){
			msg.getHeader().setMsgId(UUID.randomUUID().toString());
		}
		final String msgId=msg.getMsgHead().getMsgId();
		SendFuture<MiddleMsg> response = new SyncSendFuture(msgId);
		SyncResponseMap.syncKey.put(msgId, response);
		ClientInit.init().connect(msg,new MsgHandler() {
			@Override
			public void callback(MiddleMsg msg) {
				 SyncResponseMap.syncKey.get(msgId).setResponse(msg);
			}
		});
		response =  SyncResponseMap.syncKey.get(msgId);
		msg = response.getResponse();
		SyncResponseMap.syncKey.remove(msgId);
		return msg;
	}
	/**
	 * 获取已发送消息数量
	 * <p>Title:getMsgNum</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月1日 下午5:04:04
	 * @version 
	 * @return long
	 * @return
	 */
	@Deprecated
	public long getMsgNum(){
		return msgNum;
	}
	/**
	 * 
	 * <p>Title:isBlank</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月1日 下午5:04:20
	 * @version 
	 * @return boolean
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str){
		if(str==null || ("").equals(str.trim())){
			return true;
		}
		return false;
	}
	/*
	public static void main(String[] args) throws Exception {
		//init("99zhaiquan", "99zhaiquan", 9166, "192.168.110.183"); //初始化
		init("123", "123", 9166, "127.0.0.1"); //初始化
		for(int i=0;i<2;i++){
			//Thread.sleep(2000);
			new Thread(new Runnable() {
				@Override
				public void run() {
					BodyTest test = new BodyTest();
					test.setTestString(test.readFile());
					//构建消息
					MiddleMsg msg = new MiddleMsg("test2", 123);
					MiddleMsg msg2 = new MiddleMsg("test2", test);
					try {
						//发送消息
						ClientInit.sendMsg(msg,new MsgHandler() {
							@Override
							public void callback(MiddleMsg msg) { //回调处理
								
								logger.debug("各自处理消息体：\n"+msg.getBody());
							}
						});
						ClientInit.sendMsg(msg2,new MsgHandler() {
							@Override
							public void callback(MiddleMsg msg2) { //回调处理
								logger.debug("各自处理消息2：\n"+msg2.getBody());
							}
						});
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	*/
}
