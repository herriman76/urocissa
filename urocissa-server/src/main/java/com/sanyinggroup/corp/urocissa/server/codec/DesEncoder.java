package com.sanyinggroup.corp.urocissa.server.codec;

import java.io.UnsupportedEncodingException;
import com.sanyinggroup.corp.urocissa.core.util.DesUtil;
import com.sanyinggroup.corp.urocissa.server.ServerGlobal;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 
 * <p>Package:com.sanyinggroup.communication.server.codec</p> 
 * <p>Title:DesEncoder</p> 
 * <p>Description: des加密编码</p> 
 * @author lixiao
 * @date 2017年8月3日 下午6:41:08
 * @version
 */
public class DesEncoder extends MessageToByteEncoder<ByteBuf>{
	/**
	 * 加密编码
	 */
	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out)
			throws Exception {
		
		byte[]	bytes=new byte[msg.readableBytes()];
		msg.readBytes(bytes);
		byte[]  en = DesUtil.encrypt(bytes, ServerGlobal.desKey.getBytes());
		writeString(out, "sign");
		out.writeInt(en.length);
		out.writeBytes(en);
		
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
