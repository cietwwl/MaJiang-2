package com.zxz.config.utils;

import java.util.ResourceBundle;

public class PropertiesUtil {

	public static void main(String[] args) {
		try {
			ResourceBundle resource = ResourceBundle.getBundle("config/redisconfig");// test为属性文件名，放在包com.mmq下，如果是放在src下，直接用test即可
//			ResourceBundle resource = ResourceBundle.getBundle("com/zxz/config/utils/config");// test为属性文件名，放在包com.mmq下，如果是放在src下，直接用test即可
			String redispwd = resource.getString("redispwd");
			System.out.println(redispwd);
			String host = resource.getString("host");
			System.out.println(host);
			String port = resource.getString("port");
			System.out.println(port);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	
	
	

}
