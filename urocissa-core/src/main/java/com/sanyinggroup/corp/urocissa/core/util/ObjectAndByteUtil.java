package com.sanyinggroup.corp.urocissa.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * <p>Package:com.sanyinggroup.communication.client.util</p> 
 * <p>Title:ObjtectAndByteUtil</p> 
 * <p>Description: 对象和字符数组之间的装换</p> 
 * @author lixiao
 * @date 2017年8月8日 下午1:02:19
 * @version
 */
public class ObjectAndByteUtil {
	/**
	 * <p>Title:ObjectToByte</p> 
	 * <p>Description: 对象装字节数组</p> 
	 * @date 2017年8月8日 下午1:03:43
	 * @version 
	 * @return byte[]
	 * @param obj
	 * @return
	 */
	public static byte[] ObjectToByte(Object obj) {  
        byte[] bytes = null;  
        try {  
            // object to bytearray  
            ByteArrayOutputStream bo = new ByteArrayOutputStream();  
            ObjectOutputStream oo = new ObjectOutputStream(bo);  
            oo.writeObject(obj);  
      
            bytes = bo.toByteArray();  
      
            bo.close();  
            oo.close();  
        } catch (Exception e) {  
            System.out.println("translation" + e.getMessage());  
            e.printStackTrace();  
        }  
        return bytes;  
    } 
	/**
	 * <p>Title:ByteToObject</p> 
	 * <p>Description: 字节数组转对象</p> 
	 * @date 2017年8月8日 下午1:04:33
	 * @version 
	 * @return Object
	 * @param bytes
	 * @return
	 */
	public static Object ByteToObject(byte[] bytes) {
        Object obj = null;
        try {
            // bytearray to object
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);

            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return obj;
    }
	/**
	 * <p>Title:String2Byte</p> 
	 * <p>Description: 字符串转字节数组</p> 
	 * @date 2017年8月8日 下午1:09:49
	 * @version 
	 * @return byte[]
	 * @param str
	 * @return
	 */
	public static byte[] String2Byte(String str){
		try {
			return str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * <p>Title:byte2String</p> 
	 * <p>Description: 字节数组转字符串</p> 
	 * @date 2017年8月8日 下午1:11:45
	 * @version 
	 * @return String
	 * @param bytes
	 * @return
	 */
	public static String byte2String(byte[] bytes){
		try {
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
