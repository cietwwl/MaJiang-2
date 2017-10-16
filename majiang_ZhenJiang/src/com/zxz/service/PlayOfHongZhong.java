package com.zxz.service;

import org.json.JSONObject;

import com.zxz.domain.User;

import io.netty.channel.ChannelHandlerContext;

public interface PlayOfHongZhong {

	/**登录
	 * @param jsonObject
	 * @param session
	 * @return
	 */
	public boolean login(JSONObject jsonObject, ChannelHandlerContext session);
	
	/**创建房间
	 * @param jsonObject
	 * @param session
	 */
	public void createRoom(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**进入房间
	 * @param jsonObject
	 * @param session
	 */
	public void enterRoom(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**准备游戏
	 * @param jsonObject
	 * @param session
	 */
	public void readyGame(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**开始玩游戏
	 * @param jsonObject
	 * @param session
	 */
	public void playGame(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**房主解散房间
	 * @param jsonObject
	 * @param session
	 */
	public void disbandRoom(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**房主解散房间 
	 * @param user
	 */
	public void disbandRoom(User user);
	
	
	/**得到我自己的信息
	 * @param jsonObject
	 * @param session
	 */
	public void getMyInfo(JSONObject jsonObject, ChannelHandlerContext session);
	
	/**离开房间
	 * @param jsonObject
	 * @param session
	 */
	public void leaveRoom(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**离开房间
	 * @param user
	 */
	
	public void leaveRoom(User user);
	/**得到自己的设置信息
	 * @param jsonObject
	 * @param session
	 */
	public void getSetting(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**得到剩余的牌
	 * @param jsonObject
	 * @param session
	 */
	public void getServerInfo(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**继续游戏
	 * @param jsonObject
	 * @param session
	 */
	public void continueGame(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**设置托管
	 * @param jsonObject
	 * @param session
	 */
	public void settingAuto(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**取消托管
	 * @param jsonObject
	 * @param session
	 */
	public void cancelAuto(JSONObject jsonObject, ChannelHandlerContext session);

	/**断线重连
	 * @param jsonObject
	 * @param session
	 */
	public void downGameInfo(JSONObject jsonObject, ChannelHandlerContext session);

	/**得到用户的战绩 
	 * @param jsonObject
	 * @param session
	 */
	public void getUserScore(JSONObject jsonObject, ChannelHandlerContext session);

	/**得到总的战绩
	 * @param jsonObject
	 * @param session
	 */
	public void getUserSumScore(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**修改用户推荐号
	 * @param jsonObject
	 * @param session
	 */
	public void recommend(JSONObject jsonObject, ChannelHandlerContext session);

	/**发送消息,聊天的消息,快点啊，我等的花都谢了 
	 * @param jsonObject
	 * @param session
	 */
	public void playAudio(JSONObject jsonObject, ChannelHandlerContext session);

	
	/**设置自己的信息
	 * @param jsonObject
	 * @param session
	 */
	public void setSetting(JSONObject jsonObject, ChannelHandlerContext session);

	
	/**申请解散房间
	 * @param jsonObject
	 * @param session
	 */
	public void requestJiesan(JSONObject jsonObject, ChannelHandlerContext session);

	
	/**解散房间
	 * @param jsonObject
	 * @param session
	 */
	public void jiesanRoom(JSONObject jsonObject, ChannelHandlerContext session);

	
	/**根据用户的unionid下载游戏的信息
	 * @param jsonObject
	 * @param session
	 */
	public void downGameInfoWithUnionid(JSONObject jsonObject, ChannelHandlerContext session);
}
