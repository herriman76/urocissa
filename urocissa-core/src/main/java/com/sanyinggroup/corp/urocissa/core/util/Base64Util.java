package com.sanyinggroup.corp.urocissa.core.util;
//com.sanyinggroup.corp.urocissa.core.util

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
/**
 * 
 * <p>Package:com.sanyinggroup.corp.urocissa.core.util</p> 
 * <p>Title:Base64Util</p> 
 * <p>Description: base64加解密</p> 
 * @author lixiao
 * @date 2017年7月25日 下午3:49:14
 * @version
 */
@SuppressWarnings("restriction")
public class Base64Util {
	/**
     * Base64加密
     */
    
	public static String encryptBASE64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }

    /**
     * Base64解密
     */
    public static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }
}
