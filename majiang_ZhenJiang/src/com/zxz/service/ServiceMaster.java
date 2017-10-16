package com.zxz.service;

import org.json.JSONObject;

import io.netty.channel.ChannelHandlerContext;

public class ServiceMaster {

	JSONObject jsonObject;
	// UserService userService = new UserService();
	// PlayGameService playGameService = new PlayGameService();
	// SettingService settingService = new SettingService();
	// ServerService serverService = new ServerService();
	public ServiceMaster() {
	}
	public ServiceMaster(JSONObject jsonObject) {
		super();
		this.jsonObject = jsonObject;
	}

	public void serviceStart(ChannelHandlerContext ctx, JSONObject jsonObject) {
		String method = jsonObject.getString("m");
		if (method == null) {
			ctx.write("method is null");
			return;
		}
		hongZhongMaster(ctx, jsonObject);
	}

	private void hongZhongMaster(ChannelHandlerContext session, JSONObject jsonObject) {
		String method = jsonObject.getString("m");
		PlayOfHongZhong playOfHongZhong = new UserService();
		switch (method) {
		case "login":
			playOfHongZhong.login(jsonObject, session);// 登录
			break;
		case "cr":
			playOfHongZhong.createRoom(jsonObject, session);// 创建房间
			break;
		case "en":
			playOfHongZhong.enterRoom(jsonObject, session);// 进入房间
			break;
		case "rg":
			playOfHongZhong.readyGame(jsonObject, session);// 准备游戏
			break;
		case "playGame":
			playOfHongZhong.playGame(jsonObject, session);// 开始打牌
			break;
		case "disbandRoom":
			playOfHongZhong.disbandRoom(jsonObject, session);// 解散房间
			break;
		case "getMyInfo":
			playOfHongZhong.getMyInfo(jsonObject, session);// 得到我自己的信息
			break;
		case "leaveRoom":
			playOfHongZhong.leaveRoom(jsonObject, session);// 离开房间
			break;
		case "getSettingInfo":
			playOfHongZhong.getSetting(jsonObject, session);// 得到自己的设置信息
			break;
		case "getServerInfo":
			playOfHongZhong.getServerInfo(jsonObject, session);// 得到剩余的牌
			break;
		case "continueGame":
			playOfHongZhong.continueGame(jsonObject, session);// 继续游戏
			break;
		case "settingAuto":
			playOfHongZhong.settingAuto(jsonObject, session);// 设置托管
			break;
		case "cancelAuto":
			playOfHongZhong.cancelAuto(jsonObject, session);// 取消托管
			break;
		case "dg":
//			playOfHongZhong.downGameInfo(jsonObject, session);// 断线重连
			playOfHongZhong.downGameInfoWithUnionid(jsonObject, session);// 断线重连
			break;
		case "getUserScore":
			playOfHongZhong.getUserScore(jsonObject, session);// 得到战绩
			break;
		case "getUserSumScore":
			playOfHongZhong.getUserSumScore(jsonObject, session);// 得到总战绩
			break;
		case "recommend":
			playOfHongZhong.recommend(jsonObject, session);//保存推荐号
			break;
		case "playAudio":
			playOfHongZhong.playAudio(jsonObject, session);//发送消息
			break;
		case "setSetting":
			playOfHongZhong.setSetting(jsonObject, session);//设置自己的信息
			break;
		case "requestJiesan":
			playOfHongZhong.requestJiesan(jsonObject, session);//申请解散房间
			break;
		case "isAgreeJiesan":
			playOfHongZhong.jiesanRoom(jsonObject, session);//解散房间
			break;
		}
	}

}
