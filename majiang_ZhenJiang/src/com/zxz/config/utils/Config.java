package com.zxz.config.utils;

import java.util.ResourceBundle;

public class Config {
	
	static Config config = null;
	
	private int  Port;//游戏端口号
	private int  RPcPort;//提供远程调用的端口号
	private String localIp;//本机的地址
	private static int interval ;//查询房间时的间隔
	
	private Config() {
	}
	
	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		Config.interval = interval;
	}


	public int getPort() {
		return Port;
	}

	public void setPort(int port) {
		Port = port;
	}

	public int getRPcPort() {
		return RPcPort;
	}

	public void setRPcPort(int rPcPort) {
		RPcPort = rPcPort;
	}

	public String getLocalIp() {
		return localIp;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	public static Config getConfig(){
		if(config!=null){
			return config;
		}else{
			ResourceBundle resource = ResourceBundle.getBundle("config/otherconfig");
			config = new Config();
			config.setLocalIp(resource.getString("localIp").trim());
			int port = Integer.parseInt(resource.getString("localGamePort").trim());
			config.setPort(port);
			int rPcPort = Integer.parseInt(resource.getString("localRPCPort").trim());
			config.setRPcPort(rPcPort);
			int interval = Integer.parseInt(resource.getString("interval").trim());
			config.setInterval(interval);
			return config;
		}
	}

	@Override
	public String toString() {
		return "Config [Port=" + Port + ", RPcPort=" + RPcPort + ", localIp=" + localIp + ", interval=" + interval
				+ "]";
	}
	
	
	
}
