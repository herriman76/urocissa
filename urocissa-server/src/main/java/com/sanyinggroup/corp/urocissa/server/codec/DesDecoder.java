package com.sanyinggroup.corp.urocissa.server.codec;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.core.util.DesUtil;
import com.sanyinggroup.corp.urocissa.server.ServerGlobal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
/**
 * 
 * <p>Package:com.sanyinggroup.communication.server.codec</p> 
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
		//super(Integer.MAX_VALUE, 0, 4, 0, 4);
	}
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		//super.decode(ctx, in);
		String sign = readString(in.readInt(), in);
		loger.debug(sign);
		int length = in.readInt();
		ByteBuf desBuf = Unpooled.buffer();// 解密容器
		//ByteBuf readBytes = in.readBytes(length); //次内容为加密内容
		byte [] tem = new byte[length] ;
		if(length>1024){
			System.out.println(in.capacity());
			System.out.println(in.readableBytes());
			for(int i = 0;i<length;i++){
				//in.readBytes(1024);
				//in.readBytes(tem, 0, 1000);
				tem[i] = in.getByte(i);
				if(i/1000==0){
					in.discardReadBytes();
				}
			}
			System.out.println("==========");
			//in.readBytes(length);
		}else{
			
			in.readBytes(tem);
		}
		byte[]  en = DesUtil.decrypt(tem, ServerGlobal.desKey.getBytes());
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
