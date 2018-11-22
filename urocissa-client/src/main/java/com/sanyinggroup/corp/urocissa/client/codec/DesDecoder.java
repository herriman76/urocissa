package com.sanyinggroup.corp.urocissa.client.codec;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.client.ClientConfig;
import com.sanyinggroup.corp.urocissa.core.util.DesUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
/**
 * 
 * <p>Package:com.sanyinggroup.corp.urocissa.server.codec</p> 
 * <p>Title:DesDecoder</p> 
 * <p>Description: Des 解密 handler</p> 
 * @author lixiao
 * @date 2017年7月31日 上午11:09:14
 * @version
 */
public class DesDecoder extends LengthFieldBasedFrameDecoder{
	private static final Logger loger  = LoggerFactory.getLogger(DesDecoder.class);
	public DesDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
	}
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		ByteBuf desBuf = Unpooled.buffer();// 解密容器
		String sign = readString(in.readInt(), in);
		loger.debug(sign);
		int length = in.readInt();
		//ByteBuf readBytes = in.readBytes(length); //次内容为加密内容
		byte [] tem = new byte[length] ;
		in.readBytes(tem);
		byte[]  en = DesUtil.decrypt(tem, ClientConfig.desKey.getBytes());
		desBuf.writeBytes(en);
		return desBuf;
	}
	private String readString(int length, ByteBuf in)
			throws UnsupportedEncodingException {
		byte[] stringArray = new byte[length];
		in.readBytes(stringArray);
		return new String(stringArray, "UTF-8");
	}
}
