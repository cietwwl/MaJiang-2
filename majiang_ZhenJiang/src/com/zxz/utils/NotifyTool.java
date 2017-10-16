package com.zxz.utils;

import java.util.List;

import org.json.JSONObject;

import com.zxz.domain.OneRoom;
import com.zxz.domain.User;

import io.netty.channel.ChannelHandlerContext;


public class NotifyTool implements Constant{
	
	public static void notifyUserErrorMessage(ChannelHandlerContext session, String message) {
		session.write(message);
	}
	
	/**消息通知
	 * @param ioSession
	 * @param message
	 */
	public static void notify(ChannelHandlerContext ioSession,String message){
		JSONObject outJsonObject = new JSONObject();
		outJsonObject.put("message", message);
		ioSession.write(outJsonObject.toString());
	}
	
	
	/**消息通知
	 * @param ioSession
	 * @param message
	 */
	public static  void notify(ChannelHandlerContext ioSession,JSONObject outJsonObject){
		ioSession.write(outJsonObject.toString());
	}
	
	/**��Ϣ֪ͨ
	 * @param room
	 * @param user 
	 * @param message
	 */
	public static void notifyAllUser(OneRoom room,User user, String message){
		List<User> userList = room.getUserList();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("playId",user.getId());
		jsonObject.put("direction", user.getDirection());
		jsonObject.put("description", message);
		for(User u:userList){
			u.getIoSession().write(jsonObject.toString());
		}
	}

	
	
	/**֪ͨsessionList��ҵ���Ϣ
	 * @param sessionlist
	 * @param jsonObject
	 */
	public static void  notifyIoSessionList(List<ChannelHandlerContext> sessionlist,JSONObject jsonObject){
		for(int i=0;i<sessionlist.size();i++){
			sessionlist.get(i).write(jsonObject.toString());
		}
	}
	
}
