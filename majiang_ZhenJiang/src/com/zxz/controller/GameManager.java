package com.zxz.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zxz.domain.Game;
import com.zxz.domain.User;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * 游戏大管家，管理一个一个的游戏,key是房间的id
 *
 */
public class GameManager {
	private static final InternalLogger logger = InternalLoggerFactory.getInstance(GameManager.class);  
	public static Map<String,Game> map = new HashMap<String, Game>();
	
	public static void addGameMap(String gameId,Game game){
		map.put(gameId, game);
	}
	
	public static Map<String,Game> getGameMap(){
		return map;
	}
	
	public static List<ChannelHandlerContext> getSessionListWithRoomNumber(String roomNumber){
		Game game = map.get(roomNumber);
		List<ChannelHandlerContext> list = new ArrayList<ChannelHandlerContext>();
		if(game!=null){
			List<User> userList = game.getRoom().getUserList();
			for(User u:userList){
				list.add(u.getIoSession());
			}
			return list;
		}else{
			return null;
		}
	}
	
	/**根据房间号得到房间
	 * @param roomNumber
	 * @return
	 */
	public static Game getGameWithRoomNumber(String roomNumber){
		boolean containsKey = map.containsKey(roomNumber);
		if(containsKey){
			return map.get(roomNumber);
		}
		return null;
	}
	
	
	/**解散房间
	 * @param roomNumber
	 * @return
	 */
	public static Game removeGameWithRoomNumber(String roomNumber){
		boolean containsKey = map.containsKey(roomNumber);
		if(containsKey){
			logger.info("gameManager 房间号:"+roomNumber+"  解散");
			return map.remove(roomNumber);
		}else{
			logger.error("gameManager 移除房间失败 房间号:"+roomNumber);
		}
		return null;
	}
}
