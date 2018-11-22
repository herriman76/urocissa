
package com.sanyinggroup.corp.urocissa.server.codec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.core.model.MessageType;
import com.sanyinggroup.corp.urocissa.server.ServerConfig;
import com.sanyinggroup.corp.urocissa.server.api.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.server.api.model.MsgHead;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


/**
 * 
 * <p>Package:com.sanyinggroup.communication.server.codec</p> 
 * <p>Title:NettyMessageDecoder</p> 
 * <p>Description: 消息解码器 </p> 
 * @author lixiao
 * @date 2017年7月17日 上午11:54:27
 * @version
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {
	
	private static final Logger logger =  LoggerFactory.getLogger(MessageDecoder.class);
	MarshallingDecoder marshallingDecoder;

	public MessageDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength) throws IOException {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
		marshallingDecoder = new MarshallingDecoder();
	}
	/**
	 * 消息解码
	 * 装的什么 解什么 
	 */
	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		//System.out.println("===========消息开始解码===============");
		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
		if (frame == null) {
			return null;
		}
		MiddleMsg message = new MiddleMsg();
		MsgHead header = new MsgHead();
		header.setVersionCode(frame.readInt()); // 1
		header.setLength(frame.readInt());// 2
		header.setSessionID(readString(frame.readInt(), frame));// 3 sessionid
		header.setType(frame.readByte());// 4
		header.setAppKey(readString(frame.readInt(), frame)); //5
		header.setSign(readString(frame.readInt(), frame)); // 6
		header.setTimestamp(frame.readLong()); // 7
		header.setAction(readString(frame.readInt(), frame));//8
		header.setMsgId(readString(frame.readInt(), frame)); // 9 msgId;
		header.setStatus(frame.readInt()); //10 status
		//attachment
		int size = frame.readInt();
		if (size > 0) {
			Map<String, Object> attch = new HashMap<String, Object>(size);
			int keySize = 0;
			byte[] keyArray = null;
			String key = null;
			for (int i = 0; i < size; i++) {
				keySize = frame.readInt();
				keyArray = new byte[keySize];
				frame.readBytes(keyArray);
				key = new String(keyArray, "UTF-8");
				try {
					attch.put(key, marshallingDecoder.decode(frame));
				} catch (Exception e) {
					logger.error("非正常客户端，消息头额外参数解密失败");
				}
			}
			keyArray = null;
			key = null;
			header.setAttachment(attch);
		}
		message.setMsgHead(header);
		String secret = null; //解密秘钥
		//业务请求
		if(message.getHeader().getType() ==MessageType.CLIENT_REQ.value() || 
				message.getHeader().getType() ==MessageType.HEARTBEAT_REQ.value() 
				|| message.getHeader().getType() ==MessageType.CLIENT_RESP_FOR_SERVICE_PUSH.value()){
			secret = ServerConfig.checkSign(message.getHeader().getSessionID(), message);
			//System.out.println("解密密码："+secret);
			if(secret ==null){
				logger.debug("sessionId"+header.getSessionID());
				logger.debug("============签名验证失败========");
				header.setStatus(-103); //签名秘钥错误
			}
		}
		
		if(secret!=null){ //解密密码为null 不进行解密消息体
			if (frame.readableBytes() > 4) {
				try {
					message.setBody(marshallingDecoder.decode(frame,secret));
					/*if(header.getStatus()!=0){
						header.setStatus(200);
					}*/
					header.setStatus(200);
				} catch (Exception e) {
					//e.printStackTrace();
					logger.error("消息体解密失败",e);
					/*if(header.getStatus()!=0){
						header.setStatus(-200); //消息体解密失败
					}*/
					header.setStatus(-104); //消息体解密失败
					if(header.getAttachment()!=null){
						header.getAttachment().put("error", "消息体解密失败");
					}else{
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("error", "消息体解密失败");
						header.setAttachment(map);
					}
					message.setBody(null);
				}
			}
		}
		message.setMsgHead(header);
		frame.release();
		return message;
	}
	/**
	 * 
	 * <p>Title:readString</p> 
	 * <p>Description: 读取字符串 </p> 
	 * @date 2017年7月20日 下午4:37:19
	 * @version 
	 * @return String
	 * @param length
	 * @param frame
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String readString(int length , ByteBuf frame) throws UnsupportedEncodingException{
		byte[] stringArray = new byte[length];
		frame.readBytes(stringArray);
		return  new String(stringArray, "UTF-8");
	}
}
