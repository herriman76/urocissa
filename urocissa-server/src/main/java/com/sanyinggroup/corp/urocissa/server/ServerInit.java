package com.sanyinggroup.corp.urocissa.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.server.api.handler.HeartBeatRespHandler;
import com.sanyinggroup.corp.urocissa.server.api.handler.LoginAuthRespHandler;
import com.sanyinggroup.corp.urocissa.server.api.handler.ServiceHandler;
import com.sanyinggroup.corp.urocissa.server.api.info.ClientApp;
import com.sanyinggroup.corp.urocissa.server.api.service.MsgServiceHandler;
import com.sanyinggroup.corp.urocissa.server.api.service.MsgServiceHandlerRegister;
import com.sanyinggroup.corp.urocissa.server.codec.MessageDecoder;
import com.sanyinggroup.corp.urocissa.server.codec.MessageEncoder;
import com.sanyinggroup.corp.urocissa.server.event.EventUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
/**
 * 
 * <p>Package:com.sanyinggroup.communication.server</p> 
 * <p>Title:ServerInit</p> 
 * <p>Description: 服务端初始化</p> 
 * @author lixiao
 * @date 2017年7月21日 上午9:10:33
 * @version
 */
public class ServerInit {
	
	private EventLoopGroup bossGroup ;
	private EventLoopGroup workerGroup ;
	private ServerBootstrap b = new ServerBootstrap(); //server启动
	private static Boolean lock= true; //用户单利线程锁
	private static volatile ServerInit serviceInit;
	private static final Logger logger =LoggerFactory.getLogger(ServerInit.class);
	// 执行线程 private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private static ExecutorService  fixedThreadPool =  Executors.newFixedThreadPool(1); 
	private static volatile boolean isStarting = false;
	private volatile boolean  res = false;
	private volatile static boolean isStopping  = false;
	private volatile static MsgServiceHandlerRegister register = MsgServiceHandlerRegister.getRegister();
    private volatile static CountDownLatch latch = new CountDownLatch(1);
	private ServerInit() {
		super();
		workerGroup = new NioEventLoopGroup();
		bossGroup = new NioEventLoopGroup();
	}

	private static ServerInit init(final int port) throws Exception {
		if (serviceInit == null &&  isStarting == false) {
			isStarting = true;
			synchronized (lock) {
				if(serviceInit ==null){
					//老版本启动方式如下两行
					/*
					serviceInit = new ServerInit();
					serviceInit.start(port);
					*/
					fixedThreadPool.execute(new Runnable() {
						@Override
						public void run() {
							try {
								if(serviceInit==null) {
									serviceInit = new ServerInit();
									serviceInit.start(port);
								}
							} catch (Exception e) {
								if(!isStopping) {
									serviceInit.res =false;
									latch.countDown();
									logger.error("消息中间件urocissa-server启动失败：", e);
								}else {
									logger.error("消息中间件urocissa-server停止完成：", e);
								}
							}
							
						}
					});
					
					try {
						// 等待线程启动，3秒超时
						latch.await(5, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
			}
		}
		return serviceInit;
	}
	/*
	private static synchronized ServerInit init(final int port) throws Exception {
		logger.debug("\n======================初始化中============");
		if (serviceInit == null && isStarting ==false) {
			isStarting = true;
			serviceInit = new ServerInit();
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						serviceInit.start(port);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
		}
		return serviceInit;
	}
	*/
	/**
	 * <p>Title:init</p> 
	 * <p>Description: 默认启动方式，需要手动调用ServerConfig.setAppkeys方法 </p> 
	 * @date 2017年8月2日 下午1:45:19
	 * @version 
	 * @return ServerInit
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public static ServerInit init() throws Exception {
		if(!ServerConfig.isInit())
			ServerConfig.init("",-1,new HashMap<String,ClientApp>(0)); //如果没有设置启动参数，默认设置
		return init(ServerConfig.getPORT());
	}
	/**
	 * 
	 * <p>Title:init</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月2日 下午1:49:56
	 * @version 
	 * @return ServerInit
	 * @param configPath  properties文件路径        默认初始化，加载 config.properties
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public static ServerInit init(String configPath) throws Exception {
		ServerConfig.init(configPath); //如果没有设置启动参数，默认设置
		return init(ServerConfig.getPORT());
	}
	/**
	 * 
	 * <p>Title:init</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月2日 下午1:43:52
	 * @version 
	 * @return ServerInit
	 * @param ip 监听ip 默认本机 127.0.0.1
	 * @param port 默认 9166
	 * @param appKeys @see Map<appkey,ClientApp>
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public static ServerInit init(String ip,int port,Map<String,ClientApp> appKeys) throws Exception {
		ServerConfig.init(ip,port,appKeys); //如果没有设置启动参数，默认设置
		return init(ServerConfig.getPORT());
	}
	/**
	 * 
	 * <p>Title:init</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月2日 下午1:43:52
	 * @since 2.0.0 
	 * @return ServerInit
	 * @param ip 监听ip 默认本机 127.0.0.1
	 * @param port 默认 9166
	 * @param appKeys @see ClientApp[]
	 * @return
	 * @throws Exception
	 */
	public static ServerInit init(String ip,int port,ClientApp ...apps) throws Exception {
		ServerConfig.init(ip,port,apps); //如果没有设置启动参数，默认设置
		return init(ServerConfig.getPORT());
	}
	/**
	 * <p>Title:init</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月30日 下午3:58:10
	 * @return ServerInit
	 * @param port
	 * @param appKeys
	 * @return
	 * @throws Exception
	 * @since 1.0.0
	 */
	@Deprecated
	public static ServerInit init(int port,Map<String,ClientApp> appKeys) throws Exception {
		init("127.0.0.1",port,appKeys);
		return init(ServerConfig.getPORT());
	}
	/**
	 * @author lixiao create at 2018年1月10日 下午5:51:49 
	 * @since 2.0.0
	 * @param port
	 * @param appsList 客户端apps信息
	 * @return
	 * @throws Exception
	 */
	public static ServerInit init(int port,final List<ClientApp> appsList) throws Exception {
		ClientApp[] apps = null;
		if(appsList!=null && appsList.size()>0) {
			apps = new ClientApp[appsList.size()];
			for(int i=0;i<appsList.size();i++) {
				apps[i] = appsList.get(i);
			}
		}
		return init( port, apps); 
	}
	/**
	 * @author lixiao create at 2018年1月10日 下午5:51:49 
	 * @since 2.0.0
	 * @param port
	 * @param apps 客户端apps信息
	 * @return
	 * @throws Exception
	 */
	public static ServerInit init(int port,final ClientApp...apps) throws Exception {
		return  init("127.0.0.1",port,apps);
		//return init(ServerConfig.getPORT());
	}
	
	/**
	 * <p>Title:init</p> 
	 * <p>Description: 初始化 </p> 
	 * @date 2017年10月27日 下午2:43:20
	 * @param port
	 * @param apps
	 * @param blackList
	 * @param whiteList
	 * @param inboundRule
	 * @throws Exception
	 * @since
	 */
	public static synchronized boolean init(final int port,List<ClientApp> apps,List<String> blackList, List<String> whiteList,
			int inboundRule,Map<String,MsgServiceHandler> handlers) {
		Map<String,ClientApp> appKeys =new HashMap<String, ClientApp>();
		if(apps!=null && apps.size()>0){
			for(ClientApp app: apps){
				appKeys.put(app.getAppKey(), app);
			}
		}
		//init("127.0.0.1",port,appKeys);
		ServerConfig.setPORT(port);
		ServerConfig.setAppKeys(appKeys);
		ServerConfig.setBlackList(blackList);
		ServerConfig.setWhiteList(whiteList);
		ServerConfig.setInboundRule(inboundRule);
		MsgServiceHandlerRegister register =MsgServiceHandlerRegister.getRegister();
		register.addMsgServiceHandler(handlers);
		if(serviceInit==null){
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						if(serviceInit==null) {
							//init(ServerConfig.getPORT());
							serviceInit = new ServerInit();
							serviceInit.start(port);
						}
					} catch (Exception e) {
						if(!isStopping) {
							serviceInit.res =false;
							latch.countDown();
							logger.error("消息中间件urocissa-server启动失败：", e);
						}else {
							logger.error("消息中间件urocissa-server停止完成：", e);
						}
					}
					
				}
			});
			/*int tem = 3;
			while(tem>0 && (serviceInit==null || serviceInit.res==false)){
				try {
					Thread.sleep(1000);
					--tem;
				} catch (InterruptedException e) {
					logger.warn("线程被打断");
				}
			}*/
			try {
				// 等待线程启动，3秒超时
				latch.await(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(serviceInit==null){
				return false;
			}else{
				return serviceInit.res;
			}
		}else{
			logger.warn("消息中间件urocissa-server,已经启动，不做重复启动");
			return true;
		}
		
	}
	
	public MsgServiceHandlerRegister getRegister(){
		return register;
	}
	private void start(int port) throws Exception  {
		
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 100)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch)
							throws IOException {
						ch.pipeline().addLast(new MessageDecoder(2000*1024 * 1024, 4, 4));
						ch.pipeline().addLast(new MessageEncoder());
						//ch.pipeline().addLast("readTimeoutHandler",new ReadTimeoutHandler(120));
						ch.pipeline().addLast(new LoginAuthRespHandler());
						ch.pipeline().addLast("HeartBeatHandler",new HeartBeatRespHandler());
						ch.pipeline().addLast("handler", new ServiceHandler());
					}
				}).option(ChannelOption.SO_BACKLOG, 256);
		//b.option(ChannelOption.SO_KEEPALIVE, true);
		b.childOption(ChannelOption.SO_KEEPALIVE, true);
		try {
			// 绑定端口，同步等待成功
			ChannelFuture future = b.bind(port).sync();
			logger.info("消息中间urocissa-server初始化完成，绑定端口为 ："+port);
			serviceInit.res = true;
			latch.countDown();
			future.channel().closeFuture().sync();
		} finally {
			logger.info("消息中间件urocissa-server 即将关闭通信通道，待处理完所有消息，即将停止组件运行！！！");
			if(bossGroup!=null && !bossGroup.isShutdown())
			bossGroup.shutdownGracefully();
			if(workerGroup!=null && !workerGroup.isShutdown())
			workerGroup.shutdownGracefully();
			EventUtil.serverClose(MsgServiceHandlerRegister.getEventHandlerClass());
			logger.info("通信组件消息中间件urocissa-server关闭！");
			isStarting = false;
			serviceInit = null;
			//serviceInit.res = false;
			//通知调用这端，关闭组件
			
		}
		
	}
	/**
	 * <p>Title:shutdown</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月16日 下午3:54:49
	 * @version 
	 * @return void
	 */
	public static void shutdown(){
		logger.debug("通信组件消息中间件urocissa-server即将关闭！");
		isStopping = true;
		if(serviceInit!=null){
			serviceInit.res = false;
			if(serviceInit.bossGroup!=null && !serviceInit.bossGroup.isShutdown())
			serviceInit.bossGroup.shutdownGracefully();
			if(serviceInit.workerGroup!=null && !serviceInit.workerGroup.isShutdown())
			serviceInit.workerGroup.shutdownGracefully();
			serviceInit.bossGroup = null;
			serviceInit.workerGroup = null;
			serviceInit = null;
			logger.debug("通信组件消息中间件urocissa-server关闭！");
		}
		fixedThreadPool.shutdownNow();
	}
	
	

}
