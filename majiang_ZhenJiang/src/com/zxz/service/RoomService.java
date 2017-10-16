package com.zxz.service;

import org.json.JSONObject;

import com.zxz.controller.GameManager;
import com.zxz.controller.RoomManager;
import com.zxz.domain.Game;
import com.zxz.domain.User;
import com.zxz.utils.Constant;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * 
 *
 */
public class RoomService implements Constant{

	/**解散房间 
	 * @param ctx
	 * @param jsonObject
	 */
	public void disbandRoom(ChannelHandlerContext ctx,JSONObject jsonObject){
		User user = ctx.channel().attr(AttributeKey.<User>valueOf("user")).get();
		String roomId = user.getRoomId();//用户当前的房间号
		//查看房间是否已经占用
		Game game = GameManager.getGameWithRoomNumber(roomId);
		if(game==null){//游戏未开始
			boolean result = RoomManager.removeOneRoom(roomId);//房间不存在
			if(!result){
				JSONObject outJsonObject = new JSONObject();
				outJsonObject.put(code, error);
				outJsonObject.put(discription, "你还没有进入房间");
				ctx.write(outJsonObject.toString());
			}
		}else{
			JSONObject outJsonObject = new JSONObject();
			outJsonObject.put(code, error);
			outJsonObject.put(discription, "游戏已开始！接着玩吧");
			ctx.write(outJsonObject.toString());
		}
	}
	
}
