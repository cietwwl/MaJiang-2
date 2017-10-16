package com.zxz.service;

import org.json.JSONObject;

import com.zxz.domain.User;

import io.netty.channel.ChannelHandlerContext;

public interface PlayOfHongZhong {

	/**��¼
	 * @param jsonObject
	 * @param session
	 * @return
	 */
	public boolean login(JSONObject jsonObject, ChannelHandlerContext session);
	
	/**��������
	 * @param jsonObject
	 * @param session
	 */
	public void createRoom(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**���뷿��
	 * @param jsonObject
	 * @param session
	 */
	public void enterRoom(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**׼����Ϸ
	 * @param jsonObject
	 * @param session
	 */
	public void readyGame(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**��ʼ����Ϸ
	 * @param jsonObject
	 * @param session
	 */
	public void playGame(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**������ɢ����
	 * @param jsonObject
	 * @param session
	 */
	public void disbandRoom(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**������ɢ���� 
	 * @param user
	 */
	public void disbandRoom(User user);
	
	
	/**�õ����Լ�����Ϣ
	 * @param jsonObject
	 * @param session
	 */
	public void getMyInfo(JSONObject jsonObject, ChannelHandlerContext session);
	
	/**�뿪����
	 * @param jsonObject
	 * @param session
	 */
	public void leaveRoom(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**�뿪����
	 * @param user
	 */
	
	public void leaveRoom(User user);
	/**�õ��Լ���������Ϣ
	 * @param jsonObject
	 * @param session
	 */
	public void getSetting(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**�õ�ʣ�����
	 * @param jsonObject
	 * @param session
	 */
	public void getServerInfo(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**������Ϸ
	 * @param jsonObject
	 * @param session
	 */
	public void continueGame(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**�����й�
	 * @param jsonObject
	 * @param session
	 */
	public void settingAuto(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**ȡ���й�
	 * @param jsonObject
	 * @param session
	 */
	public void cancelAuto(JSONObject jsonObject, ChannelHandlerContext session);

	/**��������
	 * @param jsonObject
	 * @param session
	 */
	public void downGameInfo(JSONObject jsonObject, ChannelHandlerContext session);

	/**�õ��û���ս�� 
	 * @param jsonObject
	 * @param session
	 */
	public void getUserScore(JSONObject jsonObject, ChannelHandlerContext session);

	/**�õ��ܵ�ս��
	 * @param jsonObject
	 * @param session
	 */
	public void getUserSumScore(JSONObject jsonObject, ChannelHandlerContext session);
	
	
	/**�޸��û��Ƽ���
	 * @param jsonObject
	 * @param session
	 */
	public void recommend(JSONObject jsonObject, ChannelHandlerContext session);

	/**������Ϣ,�������Ϣ,��㰡���ҵȵĻ���л�� 
	 * @param jsonObject
	 * @param session
	 */
	public void playAudio(JSONObject jsonObject, ChannelHandlerContext session);

	
	/**�����Լ�����Ϣ
	 * @param jsonObject
	 * @param session
	 */
	public void setSetting(JSONObject jsonObject, ChannelHandlerContext session);

	
	/**�����ɢ����
	 * @param jsonObject
	 * @param session
	 */
	public void requestJiesan(JSONObject jsonObject, ChannelHandlerContext session);

	
	/**��ɢ����
	 * @param jsonObject
	 * @param session
	 */
	public void jiesanRoom(JSONObject jsonObject, ChannelHandlerContext session);

	
	/**�����û���unionid������Ϸ����Ϣ
	 * @param jsonObject
	 * @param session
	 */
	public void downGameInfoWithUnionid(JSONObject jsonObject, ChannelHandlerContext session);
}
