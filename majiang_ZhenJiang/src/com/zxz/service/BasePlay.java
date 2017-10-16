package com.zxz.service;

import org.json.JSONObject;

import io.netty.channel.ChannelHandlerContext;

public abstract class BasePlay extends BaseService implements PlayOfHongZhong{

	
	SettingService settingService = new SettingService();
	ServerService serverService = new ServerService();
	ScoreService scoreService = new ScoreService();
	MessageService messageService = new MessageService();
	
	
	@Override
	public void getSetting(JSONObject jsonObject, ChannelHandlerContext session) {
		settingService.getSetting(jsonObject, session);
	}


	@Override
	public void getServerInfo(JSONObject jsonObject, ChannelHandlerContext session) {
		serverService.getServerInfo(jsonObject, session);
	}
	
	@Override
	public void getUserScore(JSONObject jsonObject, ChannelHandlerContext session) {
		scoreService.getUserScore(jsonObject,session);
	}
	
	/**�õ��ܵ�ս��
	 * @param jsonObject
	 * @param session
	 */
	public void getUserSumScore(JSONObject jsonObject, ChannelHandlerContext session){
		scoreService.getUserSumScore(jsonObject,session);
	}
	
	
	@Override
	public void recommend(JSONObject jsonObject, ChannelHandlerContext session) {
		settingService.recommend(jsonObject,session);
	}
	
	
	@Override
	public void playAudio(JSONObject jsonObject, ChannelHandlerContext session) {
		messageService.playAudio(jsonObject,session);
	}


	@Override
	public void setSetting(JSONObject jsonObject, ChannelHandlerContext session) {
		serverService.setSetting(jsonObject,session);
	}


	@Override
	public void requestJiesan(JSONObject jsonObject, ChannelHandlerContext session) {
		messageService.requestJiesan(jsonObject,session);
	}


	@Override
	public void jiesanRoom(JSONObject jsonObject, ChannelHandlerContext session) {
		messageService.jiesanRoom(jsonObject,session);
	}
}
