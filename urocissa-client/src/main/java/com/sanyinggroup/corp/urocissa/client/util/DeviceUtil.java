package com.sanyinggroup.corp.urocissa.client.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.HashMap;
import java.util.Map;

import com.sanyinggroup.corp.urocissa.core.util.SignTool;

public class DeviceUtil {
	
	private static final String osName = System.getProperty("os.name");
	private static final String hostName = System.getenv().get("COMPUTERNAME");
	private static String localIp;
	private static  String macAddress;
	private static String deviceId;
	private static String prefix="";

	private DeviceUtil() {
		
	}
	
	static {
		InetAddress ia=null;  
        try {  
            ia=InetAddress.getLocalHost();  
            localIp=ia.getHostAddress();  
            System.out.println("本机名称是："+ hostName);  
            System.out.println("本机的ip是 ："+localIp);  
            InetAddress ia1 = InetAddress.getLocalHost();//获取本地IP对象    
            macAddress = getMACAddress(ia1);
            deviceId = SignTool.sign(getDeviceInfo());
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
	}
	
	
	public static void main(String[] args) throws Exception {  
        // TODO Auto-generated method stub  
		System.out.println("本机系统："+osName);
		System.out.println("本机名称是："+ hostName);  
		System.out.println("本机的ip是 ："+localIp);  
        System.out.println("MAC ......... "+macAddress);    
        System.out.println(getDeviceInfoWithId());    
    }  
	//获取MAC地址的方法    
    private static String getMACAddress(InetAddress ia)throws Exception{    
        //获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。    
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();    
        //下面代码是把mac地址拼装成String    
        StringBuffer sb = new StringBuffer();    
        if(mac!=null) {
        	for(int i=0;i<mac.length;i++){    
        		if(i!=0){    
        			sb.append("-");    
        		}    
        		//mac[i] & 0xFF 是为了把byte转化为正整数    
        		String s = Integer.toHexString(mac[i] & 0xFF);    
        		/*  System.out.println("--------------");  
            System.out.println(s);  */
        		
        		sb.append(s.length()==1?0+s:s);    
        	}    
        }
            
        //把字符串所有小写字母改为大写成为正规的mac地址并返回    
        return sb.toString().toUpperCase();    
    }
    
	public static final String getMacAddress() {
		return macAddress;
	}

	public static final String getHostName() {
		return hostName;
	}

	public static final String getLocalIp() {
		return localIp;
	}

	public static final String getOsName() {
		return osName;
	}
	public static final String getDeviceId() {
		return prefix+"-"+deviceId;
	}
	
	public static final Map<String ,String> getDeviceInfo() {
		Map<String ,String> info= new HashMap<String ,String>();
		info.put("osName", osName);
		info.put("hostName", hostName);
		info.put("localIp", localIp);
		info.put("macAddress", macAddress);
		//info.put("deviceId", deviceId);
		return info;
	}
	public static final Map<String ,String> getDeviceInfoWithId() {
		Map<String ,String> info= getDeviceInfo();
		info.put("deviceId", prefix+"-"+deviceId);
		return info;
	}
	
	public static final void setPrefix(String prefix) {
		DeviceUtil.prefix = prefix;
	}
	

	
	
    
    
}
