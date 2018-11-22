
package com.sanyinggroup.corp.urocissa.client.codec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.client.api.exception.AbnormalSecretException;
import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.client.util.MsgSignTool;
import com.sanyinggroup.corp.urocissa.client.util.SecretManagement;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * 
 * <p>
 * Package:com.sanyinggroup.corp.urocissa.server.codec
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
		InetSocketAddress address = (InetSocketAddress) ctx.channel()
				.remoteAddress();
		int port = address.getPort();
		logger.debug(address.getAddress().getHostName()+":"+port+":"+msg.getHeader().getAppKey());
		if (msg == null || msg.getMsgHead() == null)
			throw new Exception("The encode message is null");
		sendBuf.writeInt((msg.getMsgHead().getVersionCode())); // 1
		sendBuf.writeInt((msg.getMsgHead().getLength())); // 2
		//sendBuf.writeLong((msg.getMsgHead().getSessionID())); // 3
		// sessionId
		writeString(sendBuf,msg.getMsgHead().getSessionID()); //3
		sendBuf.writeByte((msg.getMsgHead().getType())); // 4
		writeString(sendBuf,msg.getMsgHead().getAppKey());// 5 appKey
		// private String sign; //签名
		SecretManagement secretMan = SecretManageCenter.getSecretMan(address.getAddress().getHostName()+port+msg.getHeader().getAppKey());
		if(secretMan==null){
			secretMan = SecretManageCenter.getSecretMan(address.getAddress().getHostAddress()+port+msg.getHeader().getAppKey());
		}
		logger.debug("获取秘钥库："+address.getAddress().getHostName()+port+msg.getHeader().getAppKey()+":"+secretMan);
		String appSecret = "";
		if(secretMan!=null){
			appSecret = secretMan.getPresentSecret(); // 不管怎样，都用当前秘钥加密 进行签名
		}
		msg.getMsgHead().setSign(MsgSignTool.sign(appSecret, msg));
		writeString(sendBuf,msg.getMsgHead().getSign()); //6 sign 签名
		sendBuf.writeLong((msg.getMsgHead().getTimestamp())); // 7  timestamp;// 时间搓
		writeString(sendBuf,msg.getMsgHead().getAction()); //8 action; //请求操作
		
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
			if(appSecret!=null && appSecret.length()>=8){
				marshallingEncoder.encode(msg.getBody(), sendBuf,appSecret);
			}else{
				logger.error("appSecret不合法:"+appSecret);
				throw new AbnormalSecretException("appSecret不合法"+appSecret);
			}
		}else{
			sendBuf.writeInt(0);
		}
		sendBuf.setInt(4, sendBuf.readableBytes() - 8);
		//logger.debug("-----------sendBuf-----------------------\n"+sendBuf);
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
