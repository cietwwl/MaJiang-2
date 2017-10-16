package com.gs.rpc1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

import com.zxz.service.HelloServiceImpl;

public class ProviderThread implements Runnable {

	private Socket socket;

	public ProviderThread(Socket socket) {
		super();
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			try {
				String methodName = input.readUTF();
				String fullClassName = input.readUTF();
				Object service = Class.forName(fullClassName).newInstance();//类的全路径,创建实例
				Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
				Object[] arguments = (Object[]) input.readObject();
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				try {
					Method method = service.getClass().getMethod(methodName, parameterTypes);
					Object result = method.invoke(service, arguments);
					output.writeObject(result);
				} catch (Throwable t) {
					output.writeObject(t);
				} finally {
					output.close();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} finally {
				input.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
