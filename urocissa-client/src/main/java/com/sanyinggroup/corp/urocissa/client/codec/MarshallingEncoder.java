package com.sanyinggroup.corp.urocissa.client.codec;

import java.io.IOException;

import org.jboss.marshalling.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.client.ClientConfig;
import com.sanyinggroup.corp.urocissa.client.api.exception.AbnormalSecretException;
import com.sanyinggroup.corp.urocissa.core.codec.ChannelBufferByteOutput;
import com.sanyinggroup.corp.urocissa.core.codec.MarshallingCodecFactory;
import com.sanyinggroup.corp.urocissa.core.util.DesUtil;
import com.sanyinggroup.corp.urocissa.core.util.ObjectAndByteUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * 
 * <p>Package:com.sanyinggroup.corp.urocissa.client.codec</p> 
 * <p>Title:MarshallingEncoder</p> 
 * <p>Description: 使用 jboss Marshalling编码 body 实现序列化 </p> 
 * @author lixiao
 * @date 2017年7月19日 上午11:15:32
 * @version
 */
@Sharable
public class MarshallingEncoder {
	private static final Logger logger = LoggerFactory.getLogger(MarshallingEncoder.class);
	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
	Marshaller marshaller;

	public MarshallingEncoder() throws IOException {
		marshaller = MarshallingCodecFactory.buildMarshalling();
	}

	protected void encode(Object msg, ByteBuf out) throws Exception {
		try {
			// des 加密开始
			byte[] b = ObjectAndByteUtil.ObjectToByte(msg);
			try {
				msg = DesUtil.encrypt(b,ClientConfig.desKey.getBytes());
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error("密码异常，加密失败", e);
				throw new AbnormalSecretException("密码异常，加密失败");
			}
			//logger.debug("des加密成功 \n"+msg);
			//des 加密结束
			int lengthPos = out.writerIndex();
			out.writeBytes(LENGTH_PLACEHOLDER);
			ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
			marshaller.start(output);
			marshaller.writeObject(msg);
			marshaller.finish();
			out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);

			b = null;
		} finally {
			marshaller.close();
		}
	}
	/**
	 * <p>Title:encode</p> 
	 * <p>Description: 加密编码body  </p> 
	 * @date 2017年8月29日 下午6:17:03
	 * @return void
	 * @param msg
	 * @param out
	 * @param secret 加密秘钥
	 * @throws Exception
	 * @since 1.0.0
	 */
	protected void encode(Object msg, ByteBuf out,String secret) throws Exception {
		try {
			// des 加密开始
			byte[] b = ObjectAndByteUtil.ObjectToByte(msg);
			try {
				msg = DesUtil.encrypt(b,secret.getBytes());
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error("密码异常，加密失败", e);
				throw new AbnormalSecretException("密码异常，加密失败");
			}
			//logger.debug("des加密成功 \n"+msg);
			//des 加密结束
			int lengthPos = out.writerIndex();
			out.writeBytes(LENGTH_PLACEHOLDER);
			ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
			marshaller.start(output);
			marshaller.writeObject(msg);
			marshaller.finish();
			out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
			
			b = null;
		} finally {
			marshaller.close();
		}
	}
}
