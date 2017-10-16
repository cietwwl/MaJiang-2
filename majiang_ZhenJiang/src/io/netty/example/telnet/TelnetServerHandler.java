/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.example.telnet;

import org.json.JSONObject;

import com.zxz.controller.RoomManager;
import com.zxz.domain.OneRoom;
import com.zxz.domain.User;
import com.zxz.service.DateServiceImpl;
import com.zxz.service.ServiceMaster;
import com.zxz.service.UserDroppedService;
import com.zxz.utils.DateUtils;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * Handles a server-side channel.
 */
@Sharable
public class TelnetServerHandler extends SimpleChannelInboundHandler<String> {

	
	 private static final InternalLogger logger = InternalLoggerFactory.getInstance(TelnetServerHandler.class);
	
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("method", "connection");
		outJsonObject.put("discription", "建立连接完成");
    	ctx.write(outJsonObject.toString());
    	ctx.flush();
//       ctx.channel().attr(AttributeKey.<User>valueOf("user")).set(findUser);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
//    	String t= ctx.channel().attr(AttributeKey.<String>valueOf("user")).get();
    	User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		if(user!=null){
			String roomId = user.getRoomId();
			logger.info("接收的消息:"+DateUtils.getCurrentDate()+" " + ctx.channel().remoteAddress() + "\t" + message+" RID:"+roomId+" userId:"+user.getId());
		}else{
			logger.info("接收的消息:"+DateUtils.getCurrentDate()+" " + ctx.channel().remoteAddress() + "\t" + message);
		}
		JSONObject jsonObject = new JSONObject(message);
		boolean isFormat = true;
		if (isFormat) {
			new ServiceMaster().serviceStart(ctx, jsonObject);
		}
    }

    

	@Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        logger.warn(getClass().getName(), cause);
    	DateServiceImpl dateService = new DateServiceImpl();
    	dateService.subOnLineUser();//离线的时候,减少在线用户数
    	User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		if(user!=null){
			String roomId = user.getRoomId();
			if(roomId!=null&&!"".equals(roomId)){
				OneRoom oneRoom = RoomManager.getRoomWithRoomId(roomId);
				if(oneRoom!=null){
					logger.error("玩家在玩的过程中掉线..." + "掉线的玩家IP地址是:"+ctx.channel().remoteAddress()+"玩家的昵称是:"+user.getNickName()+" "+roomId);
					new UserDroppedService(ctx).userDropLineNotDisbankRoom();
				}
			}
//			ctx.channel().attr(AttributeKey.<User>valueOf("user")).set(null);
//			ctx.channel().attr(AttributeKey.<User>valueOf("OtherMesage")).set(null);
			dateService.subLoginUser();//在线登录数-1
		}
		logger.info("客户端与服务端断断开链接" + ctx.channel().remoteAddress());
    }
    
    
}
