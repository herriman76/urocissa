package com.sanyinggroup.corp.urocissa.core.util;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
/**
 * 
 * <p>Package:com.sanyinggroup.corp.urocissa.core.util</p> 
 * <p>Title:PropertyFileHandle</p> 
 * <p>Description:properties 配置文件处理 </p> 
 * @author lixiao
 * @date 2017年7月12日 下午5:03:34
 * @version
 */
public class PropertyFileHandle {
	/**
	 * 
	 * <p>Title:read</p> 
	 * <p>Description: 文件读取</p> 
	 * @date 2017年7月12日 下午5:07:29
	 * @version 1.0.0
	 * @return Map<String,String> 
	 * @param path 文件地址及文件名
	 * @throws IOException
	 */
	public static Map<String,String> read(String path) throws IOException {
		Properties prop = new Properties();
		Map<String,String> res = null;
		InputStream in = null;
		try {
			// 读取属性文件
		    in = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(path));
	    	prop.load(in); // /加载属性列表
	    	Iterator<String> it = prop.stringPropertyNames().iterator();
	    	res = new HashMap<String,String>();
	    	while (it.hasNext()) {
	    		String key = it.next();
	    		res.put(key, prop.getProperty(key));
	    	}
		} finally{
			if(in !=null ){
				in.close();
			}
		}
		return res;
	}
	/**
	 * 
	 * <p>Title:write</p> 
	 * <p>Description: 属性写入</p> 
	 * @date 2017年7月12日 下午5:08:32
	 * @version 
	 * @return void
	 * @param path
	 * @param properties
	 * @throws IOException
	 */
	public static void write(String path, final Map<String, String> properties)
			throws IOException {
		FileOutputStream oFile = null;
		try {
			Properties prop = new Properties();
			oFile = new FileOutputStream(path, true);
			Set<String> keySet = properties.keySet();
			Iterator<String> it = keySet.iterator();
			while (it.hasNext()) {
				String key = it.next();
				prop.setProperty(key, properties.get(key));
				
			}
			prop.store(oFile, "new properties writed");
		}finally{
			if(oFile!=null){
				oFile.close();
			}
		}

	}

}
