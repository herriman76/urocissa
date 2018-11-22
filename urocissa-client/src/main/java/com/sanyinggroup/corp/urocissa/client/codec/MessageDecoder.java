
package com.sanyinggroup.corp.urocissa.client.codec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.client.model.MsgHead;
import com.sanyinggroup.corp.urocissa.client.util.MsgSignTool;
import com.sanyinggroup.corp.urocissa.client.util.SecretManagement;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


/**
 * 
 * <p>Package:com.sanyinggroup.corp.urocissa.server.codec</p> 
 * <p>Title:NettyMessageDecoder</p> 
 * <p>Description: 消息解码器 </p> 
 * @author lixiao
 * @date 2017年7月17日 上午11:54:27
 * @version
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageDecoder.class);
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
		InetSocketAddress address = (InetSocketAddress) ctx.channel()
				.remoteAddress();
		//String ip = address.getAddress().getHostAddress();
		int port = address.getPort();
		
		//logger.debug("解码时获得："+address.getAddress().getHostName()+":"+ip+":"+port);
		
		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
		if (frame == null) {
			return null;
		}
		MiddleMsg message = new MiddleMsg();
		MsgHead header = new MsgHead();
		header.setVersionCode(frame.readInt()); // 1
		header.setLength(frame.readInt());// 2
		header.setSessionID(readString(frame.readInt(), frame));// 3
		header.setType(frame.readByte());// 4
		header.setAppKey(readString(frame.readInt(), frame)); //5
		header.setSign(readString(frame.readInt(), frame)); // 6
		header.setTimestamp(frame.readLong()); // 7
		header.setAction(readString(frame.readInt(), frame));//8
		header.setMsgId(readString(frame.readInt(), frame)); // 9 msgId;
		header.setStatus(frame.readInt()); //10 status

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
				attch.put(key, marshallingDecoder.decode(frame));
			}
			keyArray = null;
			key = null;
			header.setAttachment(attch);
		}
		message.setHeader(header);
		String key = address.getAddress().getHostName()+port+header.getAppKey();
		SecretManagement man = SecretManageCenter.getSecretMan(key);
		if(man == null){
			man = SecretManageCenter.getSecretMan(address.getAddress().getHostAddress()+port+header.getAppKey());
		}
		if(man !=null){
			logger.debug("sessionid"+message.getMsgHead().getSessionID());
			String secret = null;
			if(MsgSignTool.verifySign(man.getPresentSecret(), message)){
				secret = man.getPresentSecret();
				logger.debug("==使用当前秘钥进行解密验证：");
			}else if(MsgSignTool.verifySign(man.getPrevSecret(), message)){
				secret  = man.getPrevSecret();
				logger.debug("==使用前一把秘钥进行解密验证：");
			}else if(MsgSignTool.verifySign(man.getNextSecret(), message)){
				secret = man.getNextSecret();
				man.nextToPresent(); //将下一把秘钥还成当前秘钥
				logger.debug("==使用前下一把秘钥进行解密验证：");
			}
			if(secret !=null ){//客户端永远使用当前秘钥解密
				//////////////---------------待完成------------
				if (frame.readableBytes() > 4) {
					//message.setBody(marshallingDecoder.decode(frame));
					try {
						message.setBody(marshallingDecoder.decode(frame,secret));
						if(header.getStatus() == 0){
							header.setStatus(200);
						}
						logger.debug("解密成功：");
					} catch (Exception e) {
						logger.error("消息体解密失败",e);
						if(header.getStatus()!=0){
							header.setStatus(-200); //消息体解密失败
						}
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
			}else{
				logger.error("客户端签名验证失败");
				if(message.getHeader().getStatus()==200){
					message.getHeader().setStatus(-1032);
					message.getHeader().getAttachment().put("statusMsg", "客户端签名验证失败");
				}
			}
		}else{
			logger.error("秘钥中心库未获取到秘钥："+key);
			if(message.getHeader().getStatus()==200){
				message.getHeader().setStatus(-1033);
				message.getHeader().getAttachment().put("statusMsg", "秘钥中心库未获取到秘钥");
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
