
package com.sanyinggroup.corp.urocissa.server.codec;

import java.io.IOException;

import org.jboss.marshalling.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.core.codec.ChannelBufferByteOutput;
import com.sanyinggroup.corp.urocissa.core.codec.MarshallingCodecFactory;
import com.sanyinggroup.corp.urocissa.core.util.DesUtil;
import com.sanyinggroup.corp.urocissa.core.util.ObjectAndByteUtil;
import com.sanyinggroup.corp.urocissa.server.ServerGlobal;
import com.sanyinggroup.corp.urocissa.server.api.exception.AbnormalSecretException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;



/**
 * <p>Package:com.sanyinggroup.communication.server.codec</p> 
 * <p>Title:MarshallingEncoder</p> 
 * <p>Description: </p> 
 * @author lixiao
 * @date 2017年9月1日 下午5:22:36
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
			msg = DesUtil.encrypt(b,ServerGlobal.desKey.getBytes());
			logger.debug("des加密成功 \n"+msg);
			//des 加密结束
			int lengthPos = out.writerIndex();
			out.writeBytes(LENGTH_PLACEHOLDER);
			ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
			marshaller.start(output);
			marshaller.writeObject(msg);
			marshaller.finish();
			out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
			b= null;
			msg = null;
		} finally {
			marshaller.close();
		}
	}
	/**
	 * <p>Title:encode</p> 
	 * <p>Description: </p> 
	 * @date 2017年9月1日 下午5:27:04
	 * @return void
	 * @param msg
	 * @param out
	 * @param secret
	 * @throws Exception
	 * @since 1.0.0
	 */
	protected void encode(Object msg, ByteBuf out ,String secret) throws Exception {
		try {
			// des 加密开始
			byte[] b = ObjectAndByteUtil.ObjectToByte(msg);
			//msg = DesUtil.encrypt(b,secret.getBytes());
			try {
				msg = DesUtil.encrypt(b,secret.getBytes());
			} catch (Exception e) {
				logger.error("密码异常，加密失败", e);
				throw new AbnormalSecretException("密码异常，加密失败");
			}
			logger.debug("des加密成功 \n"+msg);
			//des 加密结束
			int lengthPos = out.writerIndex();
			out.writeBytes(LENGTH_PLACEHOLDER);
			ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
			marshaller.start(output);
			marshaller.writeObject(msg);
			marshaller.finish();
			out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
			b= null;
			msg = null;
		} finally {
			marshaller.close();
		}
	}
}
