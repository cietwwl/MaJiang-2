package com.zxz.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.zxz.controller.GameManager;
import com.zxz.controller.RoomManager;
import com.zxz.domain.Game;
import com.zxz.domain.OneRoom;
import com.zxz.domain.User;
import com.zxz.redis.RedisUtil;
import com.zxz.utils.Constant;
import com.zxz.utils.NotifyTool;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class MessageService implements Constant{

	
	private static Logger logger = Logger.getLogger(MessageService.class);  
	/**发送消息
	 * @param jsonObject
	 * @param session
	 */
	public void playAudio(JSONObject jsonObject, ChannelHandlerContext session) {
		String type = jsonObject.getString("type");
		int messageId = jsonObject.getInt("messageId");
		playAudio(messageId,session,type);
	}

	/**发送文字
	 * @param messageId
	 * @param session
	 */
	private void playAudio(int messageId, ChannelHandlerContext session,String type) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "playAudio");
		outJsonObject.put(Constant.type, type);
		outJsonObject.put("messageId", messageId);
		outJsonObject.put("userId", user.getId());
		outJsonObject.put(direction, user.getDirection());
		NotifyTool.notifyIoSessionList(oneRoom.getUserIoSessionList(), outJsonObject);
	}

	/**申请解散房间
	 * @param jsonObject
	 * @param session
	 */
	public void requestJiesan(JSONObject jsonObject, ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		String roomId = user.getRoomId();
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(roomId);
		Game game = GameManager.getGameWithRoomNumber(roomId);
		User gamingUser = game.getGamingUser(user.getDirection());
		int totalRequetDisbanRoom = gamingUser.getTotalRequetDisbanRoom();
		gamingUser.setTotalRequetDisbanRoom(totalRequetDisbanRoom+1);
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "requestJiesan");
		outJsonObject.put("userId", user.getId());
		outJsonObject.put(discription,"申请解散房间");
		outJsonObject.put("time",TIME_TO_START_GAME/1000);
		List<User> userList = oneRoom.getUserList();
		int disbandTotal = oneRoom.getDisbandTotal();
		int nowDisbandTotal = oneRoom.setDisbandTotal(disbandTotal+1);
		for(int i=0;i<userList.size();i++){
			User u = userList.get(i);
			if(u.getId()!=gamingUser.getId()){
				u.setIsAgreeDisbandType(0);
			}else{
				u.setIsAgreeDisbandType(1);
			}
		}
		NotifyTool.notifyIoSessionList(oneRoom.getUserIoSessionList(), outJsonObject);
		waitJieSan(roomId,nowDisbandTotal);
	}
	
	/**等待解散
	 * @param game
	 */
	private static void waitJieSan(final String roomId,final int nowDisbandTotal) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				OneRoom oneRoom = RoomManager.getRoomWithRoomId(roomId);
				if(oneRoom!=null){
					int disbandTotal = oneRoom.getDisbandTotal();
					if(nowDisbandTotal!=disbandTotal){
						timer.cancel();
						return;
					}
					List<User> userList = oneRoom.getUserList();
					int totalAgree = 0;
					for (User user : userList) {
						int agreeDisband = user.getIsAgreeDisbandType();
						logger.info(user.getNickName()+" type:"+ agreeDisband);
						if(agreeDisband==0||agreeDisband==1){//同意解散或者等待解散
							totalAgree = totalAgree+1;
						}
					}
					if(totalAgree==4){//如果是四个人全部同意 
						Game game = GameManager.getGameWithRoomNumber(roomId);
						game.setDisband(true);//游戏解散
						int total = oneRoom.getTotal();
						int zhama = oneRoom.getZhama();
						JSONObject summarizeJsonObject = PlayGameService.getSummarizeJsonObject(userList,total+1,zhama);
						//记录玩家的总成绩
						PlayGameService.recoredUserScore(summarizeJsonObject, game);
						jieSanRoom( oneRoom);
					}else{
						logger.info(totalAgree);
					}
				}
			}
		}, TIME_TO_START_GAME);
	}

	/**解散房间
	 * @param jsonObject
	 * @param session
	 */
	public void jiesanRoom(JSONObject jsonObject, ChannelHandlerContext session) {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		String roomId = user.getRoomId();
		Game game = GameManager.getGameWithRoomNumber(roomId);
		String direction = user.getDirection();
		User gameUser = game.getSeatMap().get(direction);
		int type = jsonObject.getInt("type");
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(user.getRoomId());
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "isAgreeJiesan");
		outJsonObject.put("type", type);
		outJsonObject.put(discription,"是否同意解散房间");
		outJsonObject.put("userId", user.getId());
		NotifyTool.notifyIoSessionList(oneRoom.getUserIoSessionList(), outJsonObject);
		gameUser.setIsAgreeDisbandType(type);//同意解散房间
		if(type==1){
			List<User> userList = oneRoom.getUserList();
			int totalAgree = 0 ;
			for (User u : userList) {
				if(u.getIsAgreeDisbandType()==1){
					totalAgree++;
				}
			}
			if(totalAgree==oneRoom.getUserList().size()){
				game.setDisband(true);//游戏解散
				int total = oneRoom.getTotal();
				int zhama = oneRoom.getZhama();
				JSONObject summarizeJsonObject = PlayGameService.getSummarizeJsonObject(userList,total+1,zhama);
				//记录玩家的总成绩
				PlayGameService.recoredUserScore(summarizeJsonObject, game);
				jieSanRoom( oneRoom);
			}
		}else{
			List<User> userList = oneRoom.getUserList();
			JSONObject outJsonObjectFail = new JSONObject();
			outJsonObjectFail.put(method, "jiesanRoom");
			outJsonObjectFail.put("issuccess", false);
			JSONArray userArray = new JSONArray();
			for (User u : userList) {
				int agreeDisband = u.getIsAgreeDisbandType();
				if(agreeDisband!=1){//不同意解散
					JSONObject netAggree = new JSONObject();
					netAggree.put("userId",u.getId());
					netAggree.put("nickName",u.getNickName());
					userArray.put(netAggree);
				}
			}
			outJsonObjectFail.put("notAgreeArray", userArray);//不同意的玩家集合
			outJsonObjectFail.put(discription,"解散房间失败");
			NotifyTool.notifyIoSessionList(oneRoom.getUserIoSessionList(), outJsonObjectFail);
		}
	}

	/**解散房间
	 * @param user
	 * @param type
	 * @param oneRoom
	 */
	public static void jieSanRoom( OneRoom oneRoom) {
		List<User> userList = oneRoom.getUserList();
		for(int i=0;i<userList.size();i++){
			User user = userList.get(i);
			user.clearAll();//清空用户所有的属性
			RedisUtil.delKey("usRoomId"+user.getId(), 1);
		}
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put(method, "jiesanRoom");
		outJsonObject.put("issuccess", true);
		outJsonObject.put(discription,"解散房间成功");
		NotifyTool.notifyIoSessionList(oneRoom.getUserIoSessionList(), outJsonObject);
		logger.info("投票解散房间成功,游戏停止");
		//先移除游戏中的map,后移除房间中的map 否则有空指针异常,顺序不可颠倒
		GameManager.removeGameWithRoomNumber(oneRoom.getId()+"");
		RoomManager.removeOneRoomByRoomId(oneRoom.getId()+"");
	}
}
