package com.zxz.controller;

import com.gs.rpc1.RpcProvider;
import com.zxz.config.utils.Config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.example.telnet.TelnetServerInitializer;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class StartServer {

	static final boolean SSL = System.getProperty("ssl") != null;
	
	//主要用来提供运营的接口，和实时语音调用
	static Config localconfig = Config.getConfig();
	
	static final int PORT = localconfig.getPort();

	public static void main(String[] args) throws Exception {
		//主要用来提供运营的接口，和实时语音调用
		Config localconfig = Config.getConfig();
		RpcProvider provider = new RpcProvider(localconfig.getRPcPort());
		Thread thread = new Thread(provider,"接口线程");
		thread.start();
		// Configure SSL.
		final SslContext sslCtx;
		if (SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
		} else {
			sslCtx = null;
		}
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new TelnetServerInitializer(sslCtx));
			b.bind(PORT).sync().channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
