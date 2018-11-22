package com.sanyinggroup.corp.urocissa.core.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * <p>Package:com.sanyinggroup.corp.urocissa.core.util</p> 
 * <p>Title:RSAUtil</p> 
 * <p>Description: RSA加解密工具</p> 
 * @author lixiao
 * @date 2017年7月25日 下午3:50:35
 * @version
 */
public class RSAUtil {
	public static final String ENCRYPTION_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 
     * <p>Title:initKey</p> 
     * <p>Description: 生成密钥</p> 
     * @date 2017年7月25日 下午3:54:38
     * @version 
     * @return Map<String,Object>
     * @return
     * @throws Exception
     */
    public static Map<String, Object> initKey() throws Exception {
        /* 初始化密钥生成器 */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyPairGenerator.initialize(1024);

        /* 生成密钥 */
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put("publicKey", publicKey);
        keyMap.put("privateKey", privateKey);
        return keyMap;
    }
    /**
     * 
     * <p>Title:generateKey</p> 
     * <p>Description: 生成秘钥</p> 
     * @date 2017年7月25日 下午4:44:18
     * @version 
     * @return Map<String,String>
     * @return
     * @throws Exception
     */
    public static Map<String,String> generateKey() throws Exception{
    	Map<String, String> stringKey = new HashMap<String, String>(2);
    	Map<String, Object> keyMap = initKey();
    	stringKey.put("publicKey", getPublicKey(keyMap));
    	stringKey.put("privateKey", getPrivateKey(keyMap));
    	return stringKey;
    }
    /**
     * 
     * <p>Title:getPublicKey</p> 
     * <p>Description: 取得公钥字符串</p> 
     * @date 2017年7月25日 下午3:55:14
     * @version 
     * @return String
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get("publicKey");
        return Base64Util.encryptBASE64(key.getEncoded());
    }
    
    /**
     * <p>Title:getPrivateKey</p> 
     * <p>Description: 取得私钥字符串</p> 
     * @date 2017年7月25日 下午3:55:58
     * @version 
     * @return String
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get("privateKey");
        return Base64Util.encryptBASE64(key.getEncoded());
    }
    
    
    /**
     * 
     * <p>Title:encrypt</p> 
     * <p>Description: 加密</p> 
     * @date 2017年7月25日 下午3:56:15
     * @version 
     * @return byte[]
     * @param data 
     * @param keyString 秘钥字符串
     * @param isPublic 公钥还是私钥 
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, String keyString, boolean isPublic) throws Exception {
        Map<String, Object> keyAndFactoryMap = RSAUtil.generateKeyAndFactory(keyString, isPublic);
        KeyFactory keyFactory = RSAUtil.getKeyFactory(keyAndFactoryMap);
        Key key = RSAUtil.getKey(keyAndFactoryMap);
        
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(data);
    }
    
    /**
     * 
     * <p>Title:decrypt</p> 
     * <p>Description: 对数据进行解密</p> 
     * @date 2017年7月25日 下午4:36:01
     * @version 
     * @return byte[]
     * @param data
     * @param keyString
     * @param isPublic
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, String keyString, boolean isPublic) throws Exception {
        Map<String, Object> keyAndFactoryMap = RSAUtil.generateKeyAndFactory(keyString, isPublic);
        KeyFactory keyFactory = RSAUtil.getKeyFactory(keyAndFactoryMap);
        Key key = RSAUtil.getKey(keyAndFactoryMap);
        
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(data);
    }
    
    /**
     * <p>Title:generateKeyAndFactory</p> 
     * <p>Description: 根据秘钥字符串 --生成钥匙</p> 
     * @date 2017年7月25日 下午4:37:01
     * @version 
     * @return Map<String,Object>
     * @param keyString
     * @param isPublic
     * @return
     * @throws Exception
     */
    public static Map<String, Object> generateKeyAndFactory(String keyString, boolean isPublic) throws Exception {
        byte[] keyBytes = Base64Util.decryptBASE64(keyString);
        
        KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPTION_ALGORITHM);
        Key key = null;
        if (isPublic) {
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            key = keyFactory.generatePublic(x509KeySpec);
        } else {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            key = keyFactory.generatePrivate(pkcs8KeySpec);
        }
        
        Map<String, Object> keyAndFactoryMap = new HashMap<String, Object>(2);
        keyAndFactoryMap.put("key", key);
        keyAndFactoryMap.put("keyFactory", keyFactory);
        
        return keyAndFactoryMap;
    }
    
    /**
     * <p>Title:getKey</p> 
     * <p>Description: 从钥匙工厂中获取钥匙</p> 
     * @date 2017年7月25日 下午4:38:24
     * @version 
     * @return Key
     * @param map
     * @return
     */
    public static Key getKey(Map<String, Object> map) {
        if (map.get("key") == null) {
            return null;
        }
        return (Key)map.get("key");
    }

    /**
     * 
     * <p>Title:getKeyFactory</p> 
     * <p>Description: 从指定对象中获取钥匙工厂</p> 
     * @date 2017年7月25日 下午4:45:47
     * @version 
     * @return KeyFactory
     * @param map
     * @return
     */
    public static KeyFactory getKeyFactory(Map<String, Object> map) {
        if (map.get("keyFactory") == null) {
            return null;
        }
        return (KeyFactory)map.get("keyFactory");
    }
    
    /**
     * <p>Title:sign</p> 
     * <p>Description: 对信息生成数字签名（用私钥）</p> 
     * @date 2017年7月25日 下午4:46:04
     * @version 
     * @return String
     * @param data
     * @param keyString
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String keyString) throws Exception {
        Map<String, Object> keyAndFactoryMap = RSAUtil.generateKeyAndFactory(keyString, false);
        Key key = RSAUtil.getKey(keyAndFactoryMap);
        
        PrivateKey privateKey = (PrivateKey)key;

        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);

        return Base64Util.encryptBASE64(signature.sign());
    }

    /**
     * <p>Title:verify</p> 
     * <p>Description: 对信息生成数字签名（用私钥）</p> 
     * @date 2017年7月25日 下午4:46:25
     * @version 
     * @return boolean
     * @param data
     * @param keyString
     * @param sign
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String keyString, String sign)
            throws Exception {
        Map<String, Object> keyAndFactoryMap = RSAUtil.generateKeyAndFactory(keyString, true);
        Key key = RSAUtil.getKey(keyAndFactoryMap);
        
        PublicKey publicKey = (PublicKey)key;

        // 取公钥匙对象
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);

        // 验证签名是否正常
        return signature.verify(Base64Util.decryptBASE64(sign));
    }

}
