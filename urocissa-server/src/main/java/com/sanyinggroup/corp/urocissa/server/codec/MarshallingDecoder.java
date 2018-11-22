
package com.sanyinggroup.corp.urocissa.server.codec;

import java.io.IOException;
import java.io.StreamCorruptedException;

import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.core.codec.ChannelBufferByteInput;
import com.sanyinggroup.corp.urocissa.core.codec.MarshallingCodecFactory;
import com.sanyinggroup.corp.urocissa.core.util.DesUtil;
import com.sanyinggroup.corp.urocissa.core.util.ObjectAndByteUtil;
import com.sanyinggroup.corp.urocissa.server.ServerGlobal;

import io.netty.buffer.ByteBuf;



/**
 * <p>Package:com.sanyinggroup.communication.server.codec</p> 
 * <p>Title:MarshallingDecoder</p> 
 * <p>Description: </p> 
 * @author lixiao
 * @date 2017年8月30日 下午5:23:57
 * @version
 */
public class MarshallingDecoder {
	private static final Logger logger = LoggerFactory.getLogger(MarshallingDecoder.class);
	private final Unmarshaller unmarshaller;

	/**
	 * Creates a new decoder whose maximum object size is {@code 1048576} bytes.
	 * If the size of the received object is greater than {@code 1048576} bytes,
	 * a {@link StreamCorruptedException} will be raised.
	 * 
	 * @throws IOException
	 * 
	 */
	public MarshallingDecoder() throws IOException {
		unmarshaller = MarshallingCodecFactory.buildUnMarshalling();
	}
	protected Object decode(ByteBuf in) throws Exception {
		int objectSize = in.readInt();
		ByteBuf buf = in.slice(in.readerIndex(), objectSize);
		ByteInput input = new ChannelBufferByteInput(buf);
		try {
			unmarshaller.start(input);
			Object obj = null;
			obj = unmarshaller.readObject();
			unmarshaller.finish();
			in.readerIndex(in.readerIndex() + objectSize);
			// des 解密开始
			byte[] bytes = DesUtil.decrypt((byte[]) obj, ServerGlobal.desKey.getBytes());
			obj = ObjectAndByteUtil.ByteToObject(bytes);
			logger.debug("\n des 解密成功:" + obj);
			bytes = null;
			// des 解密结束
			return obj;
		} finally {
			buf.clear();
			unmarshaller.close();
		}
	}
	/**
	 * <p>Title:decode</p> 
	 * <p>Description: </p> 
	 * @date 2017年8月30日 下午5:24:26
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
			Object obj = null;
			obj = unmarshaller.readObject();
			unmarshaller.finish();
			in.readerIndex(in.readerIndex() + objectSize);
			// des 解密开始
			logger.debug("解密秘钥："+secret);
			byte[] bytes = DesUtil.decrypt((byte[]) obj, secret.getBytes());
			obj = ObjectAndByteUtil.ByteToObject(bytes);
			//logger.debug("\n des 解密成功  \n" + obj);
			bytes = null;
			// des 解密结束
			return obj;
		} finally {
			buf.clear();
			unmarshaller.close();
		}
	}
}
