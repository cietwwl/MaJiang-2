package com.zxz.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.zxz.controller.GameManager;
import com.zxz.controller.RoomManager;
import com.zxz.dao.OneRoomDao;
import com.zxz.dao.UserDao;
import com.zxz.domain.Game;
import com.zxz.domain.OneRoom;
import com.zxz.domain.User;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * 处理用户掉线
 *
 */
public class UserDroppedService {

	ChannelHandlerContext session;
	OneRoomDao roomDao = OneRoomDao.getInstance();
	UserDao userDao = UserDao.getInstance();
	
	public UserDroppedService() {
	}

	public UserDroppedService(ChannelHandlerContext session) {
		this.session = session;
		//check();
	}

	private void check() {
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		String roomId = user.getRoomId();
		Map<String, Game> gameMap = GameManager.getGameMap();
		Game game = gameMap.get(roomId);
		if(game!=null){
			setOneRoomAndseatMapSession(game);
		}
	}
	
	
	public JSONObject getGameInfo(Game game){
		JSONObject outJsonObject = new JSONObject();
		int totalGame = game.getTotalGame();
		outJsonObject.put("totalGame", totalGame);
		
		
		return outJsonObject;
	}
	
	public void setOneRoomAndseatMapSession(Game game){
		OneRoom room = game.getRoom();
		Map<String, User> seatMap = game.getSeatMap();
		List<User> userList = room.getUserList();
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		for(int i=0;i<userList.size();i++){
			User u = userList.get(i);
			if(u.getId()==user.getId()){
				u.setIoSession(session);
				break;
			}
		}
		
		Iterator<String> iterator = seatMap.keySet().iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			User u = seatMap.get(key);
			if(u.getId()==user.getId()){
				u.setIoSession(session);
				break;
			}
		}
	}
	
	
	/**
	 * 用户掉线后，把他的房间号记录下来，然后保存到数据库中
	 */
	public void userDropLine(){
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		String roomId = user.getRoomId();
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(roomId);
		Game game = GameManager.getGameWithRoomNumber(roomId);
		if(oneRoom!=null&&game==null){//已经创建了房间，但是游戏还没有开始
			//如果是房主解散房间，如果是普通玩家,把玩家踢出房间
			PlayOfHongZhong playGame = new UserService();
			if(user.isBanker()){//房主解散房间
				playGame.disbandRoom(user);
			}else{//其他玩家离开房间
				playGame.leaveRoom(user);
			}
		}else if(game!=null && oneRoom != null){//游戏进行中离开房间
			userDao.modifyUser(user.getId(),oneRoom.getRoomNumber()+"");//记录下用户的房间号
		}
	}
	
	
	
	/**
	 * 用户掉线后，把他的房间号记录下来，然后保存到数据库中,不解散房间
	 */
	public void userDropLineNotDisbankRoom(){
		User user = session.channel().attr(AttributeKey.<User>valueOf("user")).get();
		String roomId = user.getRoomId();
		OneRoom oneRoom = RoomManager.getRoomWithRoomId(roomId);
		if(oneRoom!=null){
			User modifyUser = new User();
			modifyUser.setId(user.getId());
			modifyUser.setRoomId(roomId);
			userDao.modifyUser(user.getId(),roomId);//记录下用户的房间号
		}
		JSONObject dropLine = new JSONObject();
		dropLine.put("method", "uState");
		dropLine.put("id", user.getId());
		dropLine.put("d", user.getDirection());
		dropLine.put("type", 0);
		//通知其他用户改用户掉线
		List<User> userList = oneRoom.getUserList();
		for(int i=0;i<userList.size();i++){
			User us = userList.get(i);
			if(us.getId()!=user.getId()){
				us.getIoSession().write(dropLine.toString());
			}else{
				us.setDropLine(true);
			}
		}
	}
	
	public static void main(String[] args) {
		JSONObject dropLine = new JSONObject();
		dropLine.put("id", 10020);
		dropLine.put("d", "east");
		dropLine.put("type", 0);
		System.out.println(dropLine);
	}
}
