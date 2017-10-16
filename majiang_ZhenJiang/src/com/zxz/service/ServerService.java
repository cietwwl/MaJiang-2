package com.zxz.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.zxz.controller.GameManager;
import com.zxz.domain.Game;
import com.zxz.domain.User;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class ServerService {

	
	private static Logger logger = Logger.getLogger(ServerService.class);
	
	
	public static void main(String[] args) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("m", "getServerInfo");
		jsonObject.put("roomId", 202199);
		jsonObject.put("userId", 10023);
		int array[] = {0,35};
		jsonObject.put("cards", array);
		System.out.println(jsonObject.toString());
	}
	
	
	/**得到剩余的牌
	 * @param jsonObject
	 * @param session
	 */
	public void getServerInfo(JSONObject jsonObject, ChannelHandlerContext session) {
		int roomId = jsonObject.getInt("roomId");
		int userId = jsonObject.getInt("userId");
		Map<String, Game> gameMap = GameManager.getGameMap();
		Game game = gameMap.get(roomId+"");
		JSONArray cards = jsonObject.getJSONArray("cards");
		List<Integer> linkedListCard =  new LinkedList<>();
		for(int i=0;i<cards.length();i++){
			int card = cards.getInt(i);
			linkedListCard.add(card);
		}
		Collections.sort(linkedListCard);
		List<User> userList = game.getRoom().getUserList();
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			if(user.getId()==userId){
				List<Integer> yuanCards = user.getCards();
				user.setCards(linkedListCard);
				session.write("yuanCards:"+yuanCards+" xianCards:"+linkedListCard);
			}
		}
	}

	/**设置自己的信息
	 * @param jsonObject
	 * @param session
	 */
	public void setSetting(JSONObject jsonObject, ChannelHandlerContext session) {
		String type = jsonObject.getString("type");
		if(type.equals("closeSession")){
			closeSession(session);
		}
	}

	/**关闭socket连接
	 * @param session
	 */
	private void closeSession(ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		logger.info("客户端主动要求关闭session:"+user.getNickName());
		session.close();
	}
	
}
