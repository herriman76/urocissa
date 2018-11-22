package com.sanyinggroup.corp.urocissa.client.codec;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.client.ClientConfig;
import com.sanyinggroup.corp.urocissa.core.util.DesUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class DesEncoder extends MessageToByteEncoder<ByteBuf>{
	private static final Logger logger = LoggerFactory.getLogger(DesEncoder.class);
	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out)
			throws Exception {
		//logger.debug("----------------------------------\n"+msg);
		//logger.debug("==="+msg.array());
		byte[]	bytes=new byte[msg.readableBytes()];
		msg.readBytes(bytes);
		byte[]  en = DesUtil.encrypt(bytes, ClientConfig.desKey.getBytes());
		writeString(out, "sign");
		out.writeInt(en.length);
		out.writeBytes(en);
		//out.setInt(4, out.readableBytes() - 8);
		logger.debug("----加密成功---\n"+en);
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
