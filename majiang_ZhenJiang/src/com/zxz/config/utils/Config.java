package com.zxz.config.utils;

import java.util.ResourceBundle;

public class Config {
	
	static Config config = null;
	
	private int  Port;//��Ϸ�˿ں�
	private int  RPcPort;//�ṩԶ�̵��õĶ˿ں�
	private String localIp;//�����ĵ�ַ
	private static int interval ;//��ѯ����ʱ�ļ��
	
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
