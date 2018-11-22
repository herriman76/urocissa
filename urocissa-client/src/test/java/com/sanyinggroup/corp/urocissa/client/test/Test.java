package com.sanyinggroup.corp.urocissa.client.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Test {

	public void testcl() {
		Map<String,String> clientCenter = new ConcurrentHashMap<String,String>(2);
		clientCenter.get(null);
	}
}	
