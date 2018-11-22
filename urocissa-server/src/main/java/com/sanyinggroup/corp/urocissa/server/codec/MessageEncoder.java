
package com.sanyinggroup.corp.urocissa.server.codec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.core.util.SecretManagement;
import com.sanyinggroup.corp.urocissa.server.ServerGlobal;
import com.sanyinggroup.corp.urocissa.server.api.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.server.util.MsgSignTool;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;



/**
 * 
 * <p>
 * Package:com.sanyinggroup.communication.server.codec
 * </p>
 * <p>
 * Title:NettyMessageEncoder
 * </p>
 * <p>
 * Description: 中间消息编码器
 * </p>
 * 
 * @author lixiao
 * @date 2017年7月14日 上午11:18:58
 * @version
 */
public final class MessageEncoder extends MessageToByteEncoder<MiddleMsg> {

	private static final Logger logger = LoggerFactory.getLogger(MessageEncoder.class);
	MarshallingEncoder marshallingEncoder;
	
	public MessageEncoder() throws IOException {
		this.marshallingEncoder = new MarshallingEncoder();
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, MiddleMsg msg,
			ByteBuf sendBuf) throws Exception {
		if (msg == null || msg.getMsgHead() == null)
			throw new Exception("The encode message is null");
		msg.getHeader().setTimestamp(new Date().getTime());
		sendBuf.writeInt((msg.getMsgHead().getVersionCode())); // 1
		sendBuf.writeInt((msg.getMsgHead().getLength())); // 2
		//sendBuf.writeLong((msg.getMsgHead().getSessionID())); // 3
		// sessionId
		writeString(sendBuf,msg.getMsgHead().getSessionID()); //3
		sendBuf.writeByte((msg.getMsgHead().getType())); // 4
		//byte[] StringArray = null;
		//appKey
		writeString(sendBuf,msg.getMsgHead().getAppKey());
		String appSecret = "";
		//if(msg.getHeader().getStatus()==200){
			//由原来的status值为200才签名，转换为所有值都进行签名
			SecretManagement man = ServerGlobal.sessionWithAppKeys.get(msg.getHeader().getSessionID());
			if(man!=null){
				appSecret = man.getPresentSecret();
			}
			logger.debug("服务器端即将签名，签名密码："+appSecret);
			msg.getHeader().setSign(MsgSignTool.sign(appSecret, msg));
		//}
		// private String sing; //签名
		writeString(sendBuf,msg.getMsgHead().getSign());
		// private long timestamp;// 时间搓
		sendBuf.writeLong((msg.getMsgHead().getTimestamp())); // 7
		// private String action; //请求操作
		writeString(sendBuf,msg.getMsgHead().getAction()); //8
		
		writeString(sendBuf, msg.getMsgHead().getMsgId()); //9 msgid
		
		sendBuf.writeInt(msg.getMsgHead().getStatus()); //10 status
		
		sendBuf.writeInt((msg.getMsgHead().getAttachment().size()));// 附加参数的长度
		String key = null;
		byte[] keyArray = null;
		Object value = null;
		for (Map.Entry<String, Object> param : msg.getMsgHead().getAttachment()
				.entrySet()) {
			key = param.getKey();
			keyArray = key.getBytes("UTF-8");
			sendBuf.writeInt(keyArray.length);
			sendBuf.writeBytes(keyArray);
			value = param.getValue();
			marshallingEncoder.encode(value, sendBuf);
		}
		key = null;
		keyArray = null;
		value = null;
		if (msg.getBody() != null) {
			//marshallingEncoder.encode(msg.getBody(), sendBuf);
			if(appSecret!=null && appSecret.length()>=8){
				marshallingEncoder.encode(msg.getBody(), sendBuf,appSecret);
			}else{
				logger.error("秘钥未获取到，或appSecret不合法,注意appSecret长度必须大于8位");
				sendBuf.writeInt(0);
			}
		} else
			sendBuf.writeInt(0);
		sendBuf.setInt(4, sendBuf.readableBytes() - 8);
	}
	
	private void writeString(ByteBuf sendBuf , String str) throws UnsupportedEncodingException{
		if( null == str){
			str = "";
		}
		byte[] StringArray = null;
		StringArray =str.getBytes("UTF-8");
		sendBuf.writeInt(StringArray.length);
		sendBuf.writeBytes(StringArray); 
	}
}
