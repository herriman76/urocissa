package com.sanyinggroup.corp.urocissa.core.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * <p>
 * Package:com.sanyinggroup.corp.urocissa.core.util
 * </p>
 * <p>
 * Title:SignTool
 * </p>
 * <p>
 * Description: 签名工具类
 * </p>
 * @author lixiao
 * @date 2017年8月29日 下午4:14:50
 * @version
 */
public class SignTool {
	private static final Logger logger = LoggerFactory.getLogger(SignTool.class);
	/**
	 * <p>Title:signVerify</p> 
	 * <p>Description: 签名校验</p> 
	 * @date 2017年8月29日 下午4:16:24
	 * @return boolean
	 * @param appSecret
	 * @param params : params 中包含"sign" 签名字段
	 * @return
	 * @since
	 */
	public static boolean signVerify(String appSecret, Map<String, String> params) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("appSecret", appSecret);

		for (String key : params.keySet()) {
			if (!key.equals("sign")) {
				map.put(key, params.get(key));
			}
		}
		String sign = sign(map);
		
		if (sign.equals(params.get("sign"))) {
			return true;
		}else{
			logger.debug("传入签名sign参数值："+params.get("sign"));
			logger.debug("签名校验签名结果："+sign);
		}
		return false;
	}
	/**
	 * 
	 * <p>Title:toHexValue</p> 
	 * <p>Description: 将签名的md5 转成16位的字符串</p> 
	 * @date 2017年8月29日 下午4:17:09
	 * @return String
	 * @param messageDigest
	 * @return
	 * @since
	 */
	private static String toHexValue(byte[] messageDigest) {
		if (messageDigest == null)
			return "";
		StringBuilder hexValue = new StringBuilder();
		for (byte aMessageDigest : messageDigest) {
			int val = 0xFF & aMessageDigest;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString().toUpperCase();
	}
	
	/**
	 * <p>Title:sign</p> 
	 * <p>Description: 签名</p> 
	 * @date 2017年8月29日 下午4:18:45
	 * @return String
	 * @param params
	 * @since
	 */
	public static String sign(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String string = "";
		for (String s : keys) {
			if (!s.equals("sign")){
				string += params.get(s);
			}
		}
		String sign = "";
		//logger.debug("即将参与签名参数："+string);
		try {
			sign = toHexValue(encryptMD5(string.getBytes(Charset.forName("utf-8"))));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("md5 error");
		}
		return sign;
	}
	/**
	 * <p>Title:encryptMD5</p> 
	 * <p>Description: 加密 toMd5</p> 
	 * @date 2017年8月29日 下午4:20:01
	 * @return byte[]
	 * @param data
	 * @return
	 * @throws Exception
	 * @since
	 */
	private static byte[] encryptMD5(byte[] data) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(data);
		return md5.digest();
	}

	public static void main(String[] args) {
		String appKey = "key";
		String appSecret = "secret";

		Map<String, String> params = new HashMap<String, String>();
		params.put("appKey", appKey);
		params.put("appSecret", appSecret);
		params.put("date", new Date().getTime() + "");

		String sign = sign(params);
		System.out.println(sign);
		params.put("sign", sign);

		System.out.println(signVerify(appSecret, params));

	}
}
