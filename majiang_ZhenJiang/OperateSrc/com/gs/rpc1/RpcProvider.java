package com.gs.rpc1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class RpcProvider implements Runnable{

	private static Logger logger = Logger.getLogger(RpcProvider.class);
	/**
	 * 是否开启服务
	 */
	public static boolean isProvideSerice = true;
	int port;
	
	public static void main(String[] args) {
		
	}
	
	public RpcProvider(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(port);
			logger.warn("RPC服务器启动,监听的端口号是:"+port);
			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
			while (isProvideSerice){
				Socket socket = serverSocket.accept();
				ProviderThread providerThread = new ProviderThread(socket);
				fixedThreadPool.execute(providerThread);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
