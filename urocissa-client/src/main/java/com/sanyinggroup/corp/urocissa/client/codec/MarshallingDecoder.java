package com.sanyinggroup.corp.urocissa.client.codec;

import java.io.IOException;

import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.client.ClientConfig;
import com.sanyinggroup.corp.urocissa.core.codec.ChannelBufferByteInput;
import com.sanyinggroup.corp.urocissa.core.codec.MarshallingCodecFactory;
import com.sanyinggroup.corp.urocissa.core.util.DesUtil;
import com.sanyinggroup.corp.urocissa.core.util.ObjectAndByteUtil;

import io.netty.buffer.ByteBuf;


/**
 * 
 * <p>
 * Package:com.sanyinggroup.corp.urocissa.client.codec
 * </p>
 * <p>
 * Title:MarshallingDecoder
 * </p>
 * <p>
 * Description: 使用 jboss Marshalling 解码body体重的内容
 * </p>
 * 
 * @author lixiao
 * @date 2017年7月19日 上午11:17:01
 * @version
 */
public class MarshallingDecoder {
	
	private static final Logger logger = LoggerFactory.getLogger(MarshallingDecoder.class);
	private final Unmarshaller unmarshaller;

	public MarshallingDecoder() throws IOException {
		unmarshaller = MarshallingCodecFactory.buildUnMarshalling();
	}
	/**
	 * ci
	 * <p>Title:decode</p> 
	 * <p>Description: </p> 
	 * @date 2017年7月19日 上午11:18:34
	 * @version 
	 * @return Object
	 * @param in
	 * @return
	 * @throws Exception
	 */
	protected Object decode(ByteBuf in) throws Exception {
		int objectSize = in.readInt();
		ByteBuf buf = in.slice(in.readerIndex(), objectSize);
		ByteInput input = new ChannelBufferByteInput(buf);
		try {
			unmarshaller.start(input);
			Object obj = unmarshaller.readObject();
			unmarshaller.finish();
			in.readerIndex(in.readerIndex() + objectSize);
			//logger.debug("解码秘钥：");
			// des 解密开始
		    byte[] bytes = DesUtil.decrypt((byte[])obj, ClientConfig.desKey.getBytes());
		    obj = ObjectAndByteUtil.ByteToObject(bytes);
		    bytes = null;
		    //logger.debug("\n des 解密成功  \n"+obj);
		    // des 解密结束
			return obj;
		} finally {
			buf.clear();
		    input = null;
			unmarshaller.close();
		}
	}
	/**
	 * <p>Title:decode</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月30日 下午5:23:29
	 * @return Object
	 * @param in
	 * @param secret
	 * @return
	 * @throws Exception
	 * @since 1.0.0
	 */
	protected Object decode(ByteBuf in,String secret) throws Exception {
		int objectSize = in.readInt();
		ByteBuf buf = in.slice(in.readerIndex(), objectSize);
		ByteInput input = new ChannelBufferByteInput(buf);
		try {
			unmarshaller.start(input);
			Object obj = unmarshaller.readObject();
			unmarshaller.finish();
			in.readerIndex(in.readerIndex() + objectSize);
			logger.debug("解码秘钥："+secret);
			// des 解密开始
		    byte[] bytes = DesUtil.decrypt((byte[])obj,secret.getBytes());
		    obj = ObjectAndByteUtil.ByteToObject(bytes);
		    bytes = null;
		    //in.release();
		    //logger.debug("\n des 解密成功  \n"+obj);
		    // des 解密结束
			return obj;
		} finally {
			buf.clear();
			//buf.release();
		    input = null;
			unmarshaller.close();
		}
	}
}
