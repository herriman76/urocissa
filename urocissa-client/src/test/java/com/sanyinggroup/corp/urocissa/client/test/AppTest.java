package com.sanyinggroup.corp.urocissa.client.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanyinggroup.corp.urocissa.client.event.EventHandlerTest;
import com.sanyinggroup.corp.urocissa.client.init.Client;
import com.sanyinggroup.corp.urocissa.client.init.ClientCenter;
import com.sanyinggroup.corp.urocissa.client.model.MiddleMsg;

/**
 * 
 * @author lixiao create at 2018年1月10日 下午5:15:34 
 * @since 1.0.0
 */
public class AppTest {
	private static final Logger logger = LoggerFactory.getLogger(AppTest.class);
	public static void main(String[] args) throws Exception {
		Client client  = ClientCenter.getAClient("127.0.0.1", 9166, "123", "23423",EventHandlerTest.class);
		MiddleMsg msg = new MiddleMsg("test2", 123);
		int i=0;
		while ( i<100) {
			Thread.sleep(5000);
			logger.info("\n 开始发消息=================");
			MiddleMsg resp = client.sendMsgSync(msg);
			logger.info("\n 状态码："+resp.getHeader().getStatus()+":"+resp);
			//logger.info("\n 收到同步消息： \n"+sendMsgSync);
		}
		ClientCenter.shutdownAll();
		logger.info("=====消息中间件client端停止======");
	}
}
