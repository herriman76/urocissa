package com.sanyinggroup.corp.urocissa.client.api.handler;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.client.event.EventUtil;
import com.sanyinggroup.corp.urocissa.client.event.ServerInfo;
import com.sanyinggroup.corp.urocissa.client.init.Client;
import com.sanyinggroup.corp.urocissa.client.init.ClientCenter;
import com.sanyinggroup.corp.urocissa.client.init.StartConfig;
import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.client.model.MsgHead;
import com.sanyinggroup.corp.urocissa.core.model.MessageType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
/**
 * 客户端的
 * <p>Package:com.sanyinggroup.corp.urocissa.server.api.handler</p> 
 * <p>Title:ClientHandler</p> 
 * <p>Description: 客户端消息处理</p> 
 * @author lixiao
 * @date 2017年7月17日 上午11:27:51
 * @version
 */
public class ClientHandler extends SimpleChannelInboundHandler<MiddleMsg>{
	
	private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private static long returnCount = 0l;
    
    private MsgHandler msgHandler; 
    
    public ClientHandler(){
    	super();
    }
    public ClientHandler(MsgHandler msgHandler){
    	super();
    	this.msgHandler = msgHandler;
    }
	@Override
    protected void channelRead0(ChannelHandlerContext ctx, MiddleMsg msg) throws Exception {
        
        logger.debug("服务器返回消息：\n"+msg);
        boolean bln =false;
        if(msgHandler != null){
        	msgHandler.callback(msg);
        	bln = true;
        }else{ // 在v0.2 中增加消息处理中心，为了兼容第一版，沿用第一版处理方式
        	String key = (String) msg.getMsgHead().getAttachment().get("v02handerkey");
        	logger.debug("key :\n"+key);
        	if(("$synchandlerkey$").equals(key)){// 同步消息处理的key
        		new SyncMsgHandler().callback(msg);
        		bln = true ;
        	}else if(key!=null){ //v0.2中注册过消息处理器 key的形式为
        		MsgHandler handler = MsgHandlerCenter.getMsgHandler(key);
        		if(handler!=null){
        			handler.callback(msg);
        			bln = true;
        		}
        	}
        }
        if(msg.getHeader().getType() ==MessageType.SERVICE_RESP.value()){
        	logger.debug("已接收返回数据：" + ++returnCount+"  条数据！"+bln);
        }
        if(msg.getHeader().getType() ==MessageType.SERVICE_PUSH.value()){
        	
        	PushReceiver receiver = PushReceiverCenter.getReceiver(msg);
        	if(receiver!=null){
        		logger.debug("收到服务器推送消息"+msg);
        		MiddleMsg handleReceivedMsg = receiver.handleReceivedMsg(msg);
        		if(handleReceivedMsg!=null){
        			msg.setBody(handleReceivedMsg.getBody());
        		}
        		if(msg!=null && msg.getHeader()!=null && ("1").equals(msg.getHeader().getAttachment().get("_serverPushNeedCallback"))){
        			msg.getHeader().setType(MessageType.CLIENT_RESP_FOR_SERVICE_PUSH.value());
        			msg.getHeader().setTimestamp(new Date().getTime());
        			msg.getHeader().setVersionCode(MsgHead.VCODE);
        			ctx.channel().writeAndFlush(msg);
        		}
        	}else{
        		logger.debug("收到服务器推送消息,但客户端没有处理消息");
        	}
        }
        //ctx.close();
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        try {
        	String key = ClientCenter.channelWithKey.get(ctx.channel().id().asShortText());
        	if(key!= null ) {
        		Client client = ClientCenter.getAClient(key);
        		if(client ==null) {
        			logger.debug("连接断开");
        			return;
        		}
        		StartConfig config = client.getConfig();
        		ServerInfo info = new ServerInfo(config.getIp(), config.getPort(), config.getAppKey());
        		logger.debug(info+"服务断开连接");
        		//通知事件驱动
        		EventUtil.disconnected(info, client.getEventHandlerClass());
        	}
		} catch (Exception e) {
			logger.error("服务断开连接，清理appkey时发生异常",e);
		}finally {
			if(ctx!=null && ctx.channel()!=null)
				ctx.channel().close();
		}
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	if(cause instanceof io.netty.handler.timeout.ReadTimeoutException){
    		logger.error("长时间没有读写操作，chinnal 关闭-----");
    	}else{
    		logger.error("消息返回处理异常", cause);
    		//ctx.fireExceptionCaught(cause);
    	}
    	//发生异常关闭，发送消息的时候回自动重连
    	//ctx.close();
    }
    
}
