package com.sanyinggroup.corp.urocissa.server.codec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.sanyinggroup.corp.urocissa.core.util.DesUtil;
import com.sanyinggroup.corp.urocissa.server.api.model.MiddleMsg;
import com.sanyinggroup.corp.urocissa.server.api.model.MsgHead;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class TestCodeC {
	MarshallingEncoder marshallingEncoder;
	MarshallingDecoder marshallingDecoder;

	public TestCodeC() throws IOException {
		marshallingDecoder = new MarshallingDecoder();
		marshallingEncoder = new MarshallingEncoder();
	}

	public MiddleMsg getMessage() {
		MiddleMsg nettyMessage = new MiddleMsg();
		MsgHead header = new MsgHead();
		header.setLength(123);
		header.setSessionID(99999 + "");
		header.setType((byte) 1);
		Map<String, Object> attachment = new HashMap<String, Object>();
		for (int i = 0; i < 2; i++) {
			attachment.put("ciyt --> " + i, "测试 " + i);
		}
		header.setAttachment(attachment);
		nettyMessage.setHeader(header);
		nettyMessage.setBody("案件当时觉得----AAAAAA");
		return nettyMessage;
	}

	public ByteBuf encode(MiddleMsg msg) throws Exception {
		ByteBuf sendBuf = Unpooled.buffer();
		sendBuf.writeInt((msg.getMsgHead().getVersionCode())); // 1
		sendBuf.writeInt((msg.getMsgHead().getLength())); // 2
		// sendBuf.writeLong((msg.getMsgHead().getSessionID())); // 3
		// sessionId
		writeString(sendBuf, msg.getMsgHead().getSessionID()); // 3
		sendBuf.writeByte((msg.getMsgHead().getType())); // 4
		// byte[] StringArray = null;
		// appKey
		writeString(sendBuf, msg.getMsgHead().getAppKey());

		// private String sing; //签名
		writeString(sendBuf, msg.getMsgHead().getSign());
		// private long timestamp;// 时间搓

		sendBuf.writeLong((msg.getMsgHead().getTimestamp())); // 7
		// private String action; //请求操作
		writeString(sendBuf, msg.getMsgHead().getAction()); // 8

		writeString(sendBuf, msg.getMsgHead().getMsgId()); // 9 msgid

		sendBuf.writeInt(msg.getMsgHead().getStatus()); // 10 status

		sendBuf.writeInt((msg.getMsgHead().getAttachment().size()));// 附加参数的长度
		String key = null;
		byte[] keyArray = null;
		Object value = null;

		for (Map.Entry<String, Object> param : msg.getHeader().getAttachment()
				.entrySet()) {
			key = param.getKey();
			keyArray = key.getBytes("UTF-8");
			sendBuf.writeInt(keyArray.length);
			sendBuf.writeBytes(keyArray);
			value = param.getValue();
			marshallingEncoder.encode(value, sendBuf);
		}
		key = null;
		keyArray = null;
		value = null;
		if (msg.getBody() != null) {
			marshallingEncoder.encode(msg.getBody(), sendBuf);
		} else
			sendBuf.writeInt(0);
		sendBuf.setInt(4, sendBuf.readableBytes());
		//System.out.println("============="+sendBuf.readableBytes());
		ByteBuf returnBuf = Unpooled.buffer();
		writeString(returnBuf, "测试一下");
		byte[]  en = DesUtil.encrypt(sendBuf.array(), "123423323".getBytes());
		for (int i = 0; i < en.length; i++) {
			System.out.print(en[i]+" | ");
		}
		System.out.println("");
		System.out.println("============="+en.length);
		returnBuf.writeInt(en.length);
		returnBuf.writeBytes(en);
		return returnBuf;
	}

	public MiddleMsg decode(ByteBuf ret) throws Exception {
		
		MiddleMsg message = new MiddleMsg();
		MsgHead header = new MsgHead();
		
		ByteBuf in = Unpooled.buffer();
		System.out.println(readString(ret.readInt(), ret));
		//in.writeBytes(ret.readBytes(ret.readInt()));
		int  a = ret.readInt();
		System.out.println("a"+a );
		ByteBuf readBytes = ret.readBytes(a);
		System.out.println(readBytes.readableBytes());
		byte [] tem = new byte[a] ;
		for (int i = 0; i < a; i++) {
			tem[i] = readBytes.getByte(i);
			System.out.print(tem[i]+" | ");
		}
		System.out.println("");
		byte[]  en = DesUtil.decrypt(tem, "123423323".getBytes());
		in.writeBytes(en);
		System.out.println(in.readableBytes());
		header.setVersionCode(in.readInt()); // 1
		header.setLength(in.readInt());// 2
		header.setSessionID(readString(in.readInt(), in));// 3
		header.setType(in.readByte());// 4
		header.setAppKey(readString(in.readInt(), in)); // 5
		header.setSign(readString(in.readInt(), in)); // 6
		header.setTimestamp(in.readLong()); // 7
		header.setAction(readString(in.readInt(), in));// 8
		header.setMsgId(readString(in.readInt(), in)); // 9 msgId;
		header.setStatus(in.readInt()); // 10 status

		int size = in.readInt();
		if (size > 0) {
			Map<String, Object> attch = new HashMap<String, Object>(size);
			int keySize = 0;
			byte[] keyArray = null;
			String key = null;
			for (int i = 0; i < size; i++) {
				keySize = in.readInt();
				keyArray = new byte[keySize];
				in.readBytes(keyArray);
				key = new String(keyArray, "UTF-8");
				attch.put(key, marshallingDecoder.decode(in));
			}
			keyArray = null;
			key = null;
			header.setAttachment(attch);
		}
		if (in.readableBytes() > 4) {
			message.setBody(marshallingDecoder.decode(in));
		}
		message.setHeader(header);
		return message;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		TestCodeC testC = new TestCodeC();
		MiddleMsg message = testC.getMessage();
		System.out.println(message + "[body ] " + message.getBody());

		ByteBuf buf = testC.encode(message);
		MiddleMsg decodeMsg = testC.decode(buf);
		System.out.println(decodeMsg + "[body ] " + decodeMsg.getBody());
		System.out.println("-------------------------------------------------");

	}

	private void writeString(ByteBuf sendBuf, String str)
			throws UnsupportedEncodingException {
		if (null == str) {
			str = "";
		}
		byte[] StringArray = null;
		StringArray = str.getBytes("UTF-8");
		sendBuf.writeInt(StringArray.length);
		sendBuf.writeBytes(StringArray);
	}

	private String readString(int length, ByteBuf frame)
			throws UnsupportedEncodingException {
		byte[] stringArray = new byte[length];
		frame.readBytes(stringArray);
		return new String(stringArray, "UTF-8");
	}
}
