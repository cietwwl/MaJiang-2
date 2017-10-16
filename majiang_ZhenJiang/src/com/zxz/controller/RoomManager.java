package com.zxz.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.zxz.domain.OneRoom;
import com.zxz.redis.RedisUtil;
import com.zxz.utils.Constant;
import com.zxz.utils.NotifyTool;

import io.netty.channel.ChannelHandlerContext;


/**
 * 管理一个一个的房间,房间大管家
 *
 */
public class RoomManager implements Constant{
	private static Logger logger = Logger.getLogger(RoomManager.class);  
	private static Map<String,OneRoom> map = new LinkedHashMap<String, OneRoom>();
	
	public static void addRoomMap(String roomId,OneRoom oneRoom){
		map.put(roomId, oneRoom);
	}
	
	public static Map<String,OneRoom> getRoomMap(){
		return map;
	}
	
	
	/**解散房间
	 * @param roomId
	 */
	public static boolean removeOneRoom(String roomId){
		boolean containsKey = map.containsKey(roomId);
		if(containsKey){
			OneRoom oneRoom = map.get(roomId);
			//通知房间里的用户，房间已经解散
			List<ChannelHandlerContext> sessionList = oneRoom.getUserIoSessionList();
			JSONObject outJsonObject = new JSONObject();
			outJsonObject.put(code, success);
			outJsonObject.put(discription,"房间已解散");
			NotifyTool.notifyIoSessionList(sessionList, outJsonObject);
			map.remove(roomId);
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * @param roomId
	 * @return
	 */
	public static OneRoom removeOneRoomByRoomId(String roomId){
		OneRoom oneRoom = null;
		if(map.containsKey(roomId)){
			oneRoom = map.remove(roomId);
			logger.info("RoomManager:"+roomId+" 解散房间 ");
		}else{
			logger.fatal("移除房间失败:"+roomId);
		}
		RedisUtil.delKey("zjRoomId"+roomId,1);
		return oneRoom;
	}
	
	/**根据房间号得到房间
	 * @param roomId
	 * @return
	 */
	public static OneRoom getRoomWithRoomId(String roomId){
		return map.get(roomId);
	}
	
}
